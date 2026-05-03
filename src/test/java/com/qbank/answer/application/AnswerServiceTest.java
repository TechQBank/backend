package com.qbank.answer.application;

import com.qbank.common.AbstractIntegrationTest;
import com.qbank.answer.application.dto.AnswerRequest;
import com.qbank.answer.application.dto.AnswerResponse;
import com.qbank.answer.domain.AnswerHistoryRepository;
import com.qbank.answer.domain.AnswerRepository;
import com.qbank.common.exception.BusinessException;
import com.qbank.question.domain.*;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnswerServiceTest extends AbstractIntegrationTest {

    @Autowired AnswerService answerService;
    @Autowired AnswerRepository answerRepository;
    @Autowired AnswerHistoryRepository answerHistoryRepository;
    @Autowired QuestionRepository questionRepository;
    @Autowired UserRepository userRepository;

    User user1;
    User user2;
    Question publicQ;
    Question privateQ;

    @BeforeEach
    void setup() {
        user1 = userRepository.save(User.createOAuth("GITHUB", "uid1", "user1@test.com", "유저1"));
        user2 = userRepository.save(User.createOAuth("GITHUB", "uid2", "user2@test.com", "유저2"));
        publicQ = questionRepository.save(
                Question.create(user1.getId(), "공개 질문", CareerLevel.YEAR_1_3, Difficulty.NORMAL, Visibility.PUBLIC, null, null, null)
        );
        privateQ = questionRepository.save(
                Question.create(user1.getId(), "비공개 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PRIVATE, null, null, null)
        );
    }

    @Test
    void 답변_최초_작성_시_version_1_이력_생성() {
        AnswerResponse response = answerService.createAnswer(publicQ.getId(), new AnswerRequest("첫 번째 답변"), user1.getId());

        assertThat(response.version()).isEqualTo(1);
        assertThat(answerHistoryRepository.findAllByAnswerIdOrderByVersionDesc(response.id())).hasSize(1);
    }

    @Test
    void 답변_수정_시_version_증가_및_이력_누적() {
        answerService.createAnswer(publicQ.getId(), new AnswerRequest("첫 번째 답변"), user1.getId());
        AnswerResponse updated = answerService.updateAnswer(publicQ.getId(), new AnswerRequest("두 번째 답변"), user1.getId());

        assertThat(updated.version()).isEqualTo(2);
        assertThat(updated.content()).isEqualTo("두 번째 답변");

        var histories = answerService.getHistory(publicQ.getId(), user1.getId());
        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).version()).isEqualTo(2); // DESC 정렬, 최신이 첫 번째
    }

    @Test
    void 답변_3회_수정_시_이력_3개_누적() {
        answerService.createAnswer(publicQ.getId(), new AnswerRequest("v1"), user1.getId());
        answerService.updateAnswer(publicQ.getId(), new AnswerRequest("v2"), user1.getId());
        answerService.updateAnswer(publicQ.getId(), new AnswerRequest("v3"), user1.getId());

        var histories = answerService.getHistory(publicQ.getId(), user1.getId());

        assertThat(histories).hasSize(3);
        assertThat(histories.get(0).version()).isEqualTo(3);
        assertThat(histories.get(0).content()).isEqualTo("v3");
    }

    @Test
    void 중복_답변_작성_시_예외() {
        answerService.createAnswer(publicQ.getId(), new AnswerRequest("첫 번째 답변"), user1.getId());

        assertThatThrownBy(() -> answerService.createAnswer(publicQ.getId(), new AnswerRequest("중복 답변"), user1.getId()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 존재하지_않는_답변_조회_시_예외() {
        assertThatThrownBy(() -> answerService.getAnswer(publicQ.getId(), user1.getId()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 비공개_질문_타인_답변_작성_시_예외() {
        assertThatThrownBy(() -> answerService.createAnswer(privateQ.getId(), new AnswerRequest("타인 답변"), user2.getId()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 비공개_질문_작성자_본인은_답변_가능() {
        AnswerResponse response = answerService.createAnswer(privateQ.getId(), new AnswerRequest("본인 답변"), user1.getId());

        assertThat(response.content()).isEqualTo("본인 답변");
    }
}
