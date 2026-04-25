package com.qbank.question.application.dto;

public record QuestionStatsResponse(
        long totalQuestions,
        long myQuestions,
        long myBookmarks
) {}
