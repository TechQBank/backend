package com.qbank.question.presentation.dto;

import com.qbank.question.domain.Visibility;
import com.qbank.review.domain.ReviewStatus;

import java.util.List;

public class StudyQuestions {

    public record Request(
            String reviewStatus,
            List<Long> tagIds,
            Boolean hasAnswer,
            Boolean isMine,
            Boolean isBookmarked,
            String visibility
    ) {
        public ReviewStatus getReviewStatus() {
            return reviewStatus != null ? ReviewStatus.of(reviewStatus).orElse(null) : null;
        }

        public Visibility getVisibility() {
            return visibility != null ? Visibility.of(visibility).orElse(null) : null;
        }
    }
}
