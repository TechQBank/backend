package com.qbank.question.domain;

import com.qbank.question.application.dto.QuestionSearchCondition;
import com.qbank.review.domain.ReviewStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionRepositoryCustom {

    List<Question> findPublicQuestions(QuestionSearchCondition condition, Pageable pageable);

    List<Question> findMyQuestions(Long userId, Visibility visibility, Pageable pageable);

    List<Question> findBookmarkedQuestions(Long userId, Pageable pageable);

    List<Question> findStudyQuestions(Long userId, ReviewStatus reviewStatus, List<Long> tagIds,
                                      Boolean hasAnswer, Boolean isMine, Boolean isBookmarked,
                                      Visibility visibility, Pageable pageable);
}
