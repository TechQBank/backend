package com.qbank.answer.application.dto;

import com.qbank.answer.domain.UserQuestionAnswer;

import java.time.LocalDateTime;

public record AnswerResponse(
        Long id,
        String content,
        int version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AnswerResponse from(UserQuestionAnswer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getContent(),
                answer.getVersion(),
                answer.getCreatedAt(),
                answer.getUpdatedAt()
        );
    }
}
