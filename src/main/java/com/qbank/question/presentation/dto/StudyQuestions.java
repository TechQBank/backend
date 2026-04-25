package com.qbank.question.presentation.dto;

import com.qbank.review.domain.ReviewStatus;

import java.util.List;

public class StudyQuestions {

    public record Request(
            String reviewStatus,
            List<Long> tagIds,
            Boolean hasAnswer
    ) {
        public ReviewStatus getReviewStatus() {
            return reviewStatus != null ? ReviewStatus.of(reviewStatus).orElse(null) : null;
        }
    }
}
