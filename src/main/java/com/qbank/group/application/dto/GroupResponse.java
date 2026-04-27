package com.qbank.group.application.dto;

import com.qbank.group.domain.QuestionGroup;

import java.time.LocalDateTime;

public record GroupResponse(
        Long id,
        String name,
        String description,
        boolean isPublic,
        long questionCount,
        LocalDateTime createdAt
) {
    public static GroupResponse of(QuestionGroup group, long questionCount) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.isPublic(),
                questionCount,
                group.getCreatedAt()
        );
    }
}
