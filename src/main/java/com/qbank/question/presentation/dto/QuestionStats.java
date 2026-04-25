package com.qbank.question.presentation.dto;

import com.qbank.question.application.dto.QuestionStatsResponse;

public class QuestionStats {

    public record Response(
            long totalQuestions,
            long myQuestions,
            long myBookmarks
    ) {

        public static Response of(QuestionStatsResponse dto) {
            return new Response(dto.totalQuestions(), dto.myQuestions(), dto.myBookmarks());
        }
    }

}
