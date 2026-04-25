package com.qbank.answer.application.dto;

import com.qbank.answer.domain.AnswerHistory;

import java.time.LocalDateTime;

public record AnswerHistoryResponse(
        int version,
        String content,
        LocalDateTime createdAt
) {
    public static AnswerHistoryResponse from(AnswerHistory history) {
        return new AnswerHistoryResponse(
                history.getVersion(),
                history.getContent(),
                history.getCreatedAt()
        );
    }
}
