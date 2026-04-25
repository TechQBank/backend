package com.qbank.review.application.dto;

import com.qbank.review.domain.ReviewStatus;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "복습 상태는 필수입니다")
        String status
) {
    public ReviewStatus getStatus() {
        return status != null ? ReviewStatus.of(status).orElse(null) : null;
    }
}
