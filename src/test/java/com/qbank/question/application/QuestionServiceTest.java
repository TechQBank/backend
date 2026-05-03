package com.qbank.question.application;

import com.qbank.common.abstractclass.AbstractIntegrationTest;
import com.qbank.answer.domain.AnswerRepository;
import com.qbank.answer.domain.UserQuestionAnswer;
import com.qbank.bookmark.domain.Bookmark;
import com.qbank.bookmark.domain.BookmarkRepository;
import com.qbank.common.exception.BusinessException;
import com.qbank.common.response.SliceResponse;
import com.qbank.question.application.dto.QuestionDetail;
import com.qbank.question.application.dto.QuestionSummary;
import com.qbank.question.application.dto.StudyQuestionSummary;
import com.qbank.question.domain.*;
import com.qbank.review.domain.ReviewRepository;
import com.qbank.review.domain.ReviewStatus;
import com.qbank.review.domain.UserQuestionReview;
import com.qbank.tag.domain.Tag;
import com.qbank.tag.domain.TagRepository;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionServiceTest extends AbstractIntegrationTest {

    @Autowired QuestionService questionService;
    @Autowired QuestionRepository questionRepository;
    @Autowired QuestionTagRepository questionTagRepository;
    @Autowired TagRepository tagRepository;
    @Autowired UserRepository userRepository;
    @Autowired BookmarkRepository bookmarkRepository;
    @Autowired AnswerRepository answerRepository;
    @Autowired ReviewRepository reviewRepository;

    User user1;
    User user2;
    Tag javaTag;
    Tag springTag;
    Question publicQ;
    Question privateQ;

    @BeforeEach
    void setup() {
        user1 = userRepository.save(User.createOAuth("GITHUB", "uid1", "user1@test.com", "유저1"));
        user2 = userRepository.save(User.createOAuth("GITHUB", "uid2", "user2@test.com", "유저2"));
        javaTag = tagRepository.save(Tag.of("Java"));
        springTag = tagRepository.save(Tag.of("Spring"));
        publicQ = questionRepository.save(
                Question.create(user1.getId(), "JPA 질문", CareerLevel.YEAR_1_3, Difficulty.NORMAL, Visibility.PUBLIC, null, null, null)
        );
        privateQ = questionRepository.save(
                Question.create(user2.getId(), "비공개 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PRIVATE, null, null, null)
        );
    }

    // ─── 공개 질문 목록 조회 ─────────────────────────────────────────────

    @Test
    void 공개_질문_목록에서_비공개_질문_제외() {
        SliceResponse<QuestionSummary.Response> result = publicQuestions(null, null, null, null);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("JPA 질문");
    }

    @Test
    void 키워드로_제목_검색() {
        questionRepository.save(Question.create(user1.getId(), "Spring Boot 입문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null));

        SliceResponse<QuestionSummary.Response> result = publicQuestions("Spring", null, null, null);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).contains("Spring");
    }

    @Test
    void 키워드가_설명에_포함된_경우도_검색() {
        Question q = questionRepository.save(
                Question.create(user1.getId(), "기타 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, "GC 알고리즘 설명", null, null)
        );

        SliceResponse<QuestionSummary.Response> result = publicQuestions("기타", null, null, null);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).id()).isEqualTo(q.getId());
    }

    @Test
    void 경력수준_필터() {
        questionRepository.save(Question.create(user1.getId(), "시니어 질문", CareerLevel.YEAR_5_PLUS, Difficulty.HARD, Visibility.PUBLIC, null, null, null));

        SliceResponse<QuestionSummary.Response> result = publicQuestions(null, null, CareerLevel.YEAR_1_3, null);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).careerLevel()).isEqualTo(CareerLevel.YEAR_1_3);
    }

    @Test
    void 난이도_필터() {
        questionRepository.save(Question.create(user1.getId(), "어려운 질문", CareerLevel.YEAR_3_5, Difficulty.HARD, Visibility.PUBLIC, null, null, null));

        SliceResponse<QuestionSummary.Response> result = publicQuestions(null, null, null, Difficulty.HARD);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).difficulty()).isEqualTo(Difficulty.HARD);
    }

    @Test
    void 태그_필터_OR_조건() {
        questionTagRepository.save(QuestionTag.of(publicQ, javaTag));

        Question springQ = questionRepository.save(
                Question.create(user1.getId(), "Spring 질문", CareerLevel.YEAR_1_3, Difficulty.NORMAL, Visibility.PUBLIC, null, null, null)
        );
        questionTagRepository.save(QuestionTag.of(springQ, springTag));

        questionRepository.save(
                Question.create(user1.getId(), "태그없는 질문", CareerLevel.YEAR_1_3, Difficulty.NORMAL, Visibility.PUBLIC, null, null, null)
        );

        SliceResponse<QuestionSummary.Response> result = publicQuestions(null, List.of(javaTag.getId(), springTag.getId()), null, null);

        assertThat(result.content()).hasSize(2);
    }

    @Test
    void 경력수준과_태그_복합_필터() {
        questionTagRepository.save(QuestionTag.of(publicQ, javaTag));

        Question juniorJavaQ = questionRepository.save(
                Question.create(user1.getId(), "자바 JUNIOR 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );
        questionTagRepository.save(QuestionTag.of(juniorJavaQ, javaTag));

        // YEAR_1_3 + Java 태그 → publicQ만 반환
        QuestionSummary.Request req = new QuestionSummary.Request(
                null, List.of(javaTag.getId()), CareerLevel.YEAR_1_3, null,
                PageRequest.of(0, 20), user1.getId(), null, null
        );
        SliceResponse<QuestionSummary.Response> result = questionService.getPublicQuestions(req);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).id()).isEqualTo(publicQ.getId());
    }

    // ─── 학습 질문 (isStudyQuestion) ─────────────────────────────────────

    @Test
    void 학습질문_내가_등록한_질문_포함() {
        Question otherPublicQ = questionRepository.save(
                Question.create(user2.getId(), "타인 공개질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );

        SliceResponse<StudyQuestionSummary> result = studyQuestions(user1.getId(), null, null, null);
        List<Long> ids = result.content().stream().map(StudyQuestionSummary::id).toList();

        assertThat(ids).contains(publicQ.getId());
        assertThat(ids).doesNotContain(otherPublicQ.getId()); // 북마크/답변/복습 없으면 미포함
    }

    @Test
    void 학습질문_북마크한_질문_포함() {
        Question otherQ = questionRepository.save(
                Question.create(user2.getId(), "타인 공개질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );
        bookmarkRepository.save(Bookmark.of(user1.getId(), otherQ.getId()));

        SliceResponse<StudyQuestionSummary> result = studyQuestions(user1.getId(), null, null, null);
        List<Long> ids = result.content().stream().map(StudyQuestionSummary::id).toList();

        assertThat(ids).contains(otherQ.getId());
    }

    @Test
    void 학습질문_답변_작성한_질문_포함() {
        Question otherQ = questionRepository.save(
                Question.create(user2.getId(), "타인 공개질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );
        answerRepository.save(UserQuestionAnswer.create(user1.getId(), otherQ.getId(), "내 답변"));

        SliceResponse<StudyQuestionSummary> result = studyQuestions(user1.getId(), null, null, null);
        List<Long> ids = result.content().stream().map(StudyQuestionSummary::id).toList();

        assertThat(ids).contains(otherQ.getId());
    }

    @Test
    void 학습질문_복습_설정한_질문_포함() {
        Question otherQ = questionRepository.save(
                Question.create(user2.getId(), "타인 공개질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );
        reviewRepository.save(UserQuestionReview.create(user1.getId(), otherQ.getId(), ReviewStatus.KNOWN));

        SliceResponse<StudyQuestionSummary> result = studyQuestions(user1.getId(), null, null, null);
        List<Long> ids = result.content().stream().map(StudyQuestionSummary::id).toList();

        assertThat(ids).contains(otherQ.getId());
    }

    @Test
    void 학습질문_복습상태_필터() {
        reviewRepository.save(UserQuestionReview.create(user1.getId(), publicQ.getId(), ReviewStatus.KNOWN));

        Question unknownQ = questionRepository.save(
                Question.create(user1.getId(), "UNKNOWN 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );
        reviewRepository.save(UserQuestionReview.create(user1.getId(), unknownQ.getId(), ReviewStatus.UNKNOWN));

        SliceResponse<StudyQuestionSummary> result = studyQuestions(user1.getId(), ReviewStatus.KNOWN, null, null);
        List<Long> ids = result.content().stream().map(StudyQuestionSummary::id).toList();

        assertThat(ids).containsOnly(publicQ.getId());
    }

    @Test
    void 학습질문_답변_작성_여부_필터() {
        answerRepository.save(UserQuestionAnswer.create(user1.getId(), publicQ.getId(), "내 답변"));

        Question noAnswerQ = questionRepository.save(
                Question.create(user1.getId(), "답변없는 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );

        SliceResponse<StudyQuestionSummary> withAnswer = studyQuestions(user1.getId(), null, true, null);
        SliceResponse<StudyQuestionSummary> withoutAnswer = studyQuestions(user1.getId(), null, false, null);

        assertThat(withAnswer.content().stream().map(StudyQuestionSummary::id).toList())
                .containsOnly(publicQ.getId());
        assertThat(withoutAnswer.content().stream().map(StudyQuestionSummary::id).toList())
                .contains(noAnswerQ.getId())
                .doesNotContain(publicQ.getId());
    }

    // ─── 질문 상세 조회 ───────────────────────────────────────────────────

    @Test
    void 질문_상세_조회_내_질문() {
        questionTagRepository.save(QuestionTag.of(publicQ, javaTag));

        QuestionDetail.Response detail = questionService.getDetail(publicQ.getId(), user1.getId());

        assertThat(detail.id()).isEqualTo(publicQ.getId());
        assertThat(detail.isMyQuestion()).isTrue();
        assertThat(detail.tags()).hasSize(1);
        assertThat(detail.tags().get(0).name()).isEqualTo("Java");
    }

    @Test
    void 질문_상세_조회_북마크_여부_반영() {
        bookmarkRepository.save(Bookmark.of(user1.getId(), publicQ.getId()));

        QuestionDetail.Response detail = questionService.getDetail(publicQ.getId(), user1.getId());

        assertThat(detail.isBookmarked()).isTrue();
        assertThat(detail.bookmarkCount()).isEqualTo(1L);
    }

    @Test
    void 비공개_질문_타인_조회_시_예외() {
        assertThatThrownBy(() -> questionService.getDetail(privateQ.getId(), user1.getId()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 비공개_질문_작성자_본인은_조회_가능() {
        QuestionDetail.Response detail = questionService.getDetail(privateQ.getId(), user2.getId());

        assertThat(detail.id()).isEqualTo(privateQ.getId());
        assertThat(detail.visibility()).isEqualTo(Visibility.PRIVATE);
    }

    // ─── 헬퍼 메서드 ──────────────────────────────────────────────────────

    private SliceResponse<QuestionSummary.Response> publicQuestions(
            String keyword, List<Long> tagIds, CareerLevel careerLevel, Difficulty difficulty) {
        QuestionSummary.Request req = new QuestionSummary.Request(
                keyword, tagIds, careerLevel, difficulty,
                PageRequest.of(0, 20), user1.getId(), null, null
        );
        return questionService.getPublicQuestions(req);
    }

    private SliceResponse<StudyQuestionSummary> studyQuestions(
            Long userId, ReviewStatus reviewStatus, Boolean hasAnswer, Boolean isMine) {
        return questionService.getStudyQuestions(
                reviewStatus, null, hasAnswer, isMine, null, null,
                PageRequest.of(0, 20), userId
        );
    }
}
