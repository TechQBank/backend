package com.qbank.question.application.dto;

import com.qbank.question.domain.CareerLevel;
import com.qbank.question.domain.Difficulty;
import com.qbank.question.domain.Visibility;
import com.qbank.review.domain.ReviewStatus;

import java.time.LocalDateTime;
import java.util.List;

public record StudyQuestionSummary(
        Long id,
        String title,
        List<TagInfo> tags,
        CareerLevel careerLevel,
        Difficulty difficulty,
        Visibility visibility,
        String authorNickname,
        boolean isMyQuestion,
        boolean isBookmarked,
        long bookmarkCount,
        LocalDateTime createdAt,
        ReviewStatus reviewStatus,
        boolean hasAnswer
) {
    public record TagInfo(Long id, String name) {}
}
