package com.qbank.bookmark.application;

import com.qbank.bookmark.application.dto.BookmarkToggle;
import com.qbank.bookmark.domain.BookmarkRepository;
import com.qbank.common.abstractclass.AbstractIntegrationTest;
import com.qbank.common.exception.BusinessException;
import com.qbank.question.domain.*;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookmarkServiceTest extends AbstractIntegrationTest {

    @Autowired BookmarkService bookmarkService;
    @Autowired BookmarkRepository bookmarkRepository;
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
                Question.create(user2.getId(), "공개 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PUBLIC, null, null, null)
        );
        privateQ = questionRepository.save(
                Question.create(user2.getId(), "비공개 질문", CareerLevel.JUNIOR, Difficulty.EASY, Visibility.PRIVATE, null, null, null)
        );
    }

    @Test
    void 북마크_추가() {
        BookmarkToggle.Response response = bookmarkService.toggle(publicQ.getId(), user1.getId());

        assertThat(response.isBookmarked()).isTrue();
        assertThat(response.bookmarkCount()).isEqualTo(1L);
        assertThat(bookmarkRepository.existsByUserIdAndQuestionId(user1.getId(), publicQ.getId())).isTrue();
    }

    @Test
    void 북마크_토글_해제() {
        bookmarkService.toggle(publicQ.getId(), user1.getId()); // 추가
        BookmarkToggle.Response response = bookmarkService.toggle(publicQ.getId(), user1.getId()); // 해제

        assertThat(response.isBookmarked()).isFalse();
        assertThat(response.bookmarkCount()).isEqualTo(0L);
        assertThat(bookmarkRepository.existsByUserIdAndQuestionId(user1.getId(), publicQ.getId())).isFalse();
    }

    @Test
    void 북마크_카운트는_사용자별로_집계() {
        bookmarkService.toggle(publicQ.getId(), user1.getId());
        bookmarkService.toggle(publicQ.getId(), user2.getId());

        long count = bookmarkRepository.countByQuestionId(publicQ.getId());

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void 비공개_질문_타인_북마크_시_예외() {
        assertThatThrownBy(() -> bookmarkService.toggle(privateQ.getId(), user1.getId()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 비공개_질문_작성자_본인은_북마크_가능() {
        BookmarkToggle.Response response = bookmarkService.toggle(privateQ.getId(), user2.getId());

        assertThat(response.isBookmarked()).isTrue();
    }
}
