package com.qbank.answer.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequest(
        @NotBlank(message = "답변 내용은 필수입니다")
        String content
) {}
