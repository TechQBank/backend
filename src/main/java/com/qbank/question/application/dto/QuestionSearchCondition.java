package com.qbank.question.application.dto;

import com.qbank.question.domain.CareerLevel;
import com.qbank.question.domain.Difficulty;

import java.util.List;

public record QuestionSearchCondition(
        String keyword,
        List<Long> tagIds,
        CareerLevel careerLevel,
        Difficulty difficulty
) {

    public static QuestionSearchCondition of(QuestionSummary.Request dto) {
        return new QuestionSearchCondition(
                dto.keyword(), dto.tagIds(), dto.careerLevel(), dto.difficulty()
        );
    }
}