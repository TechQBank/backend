package com.qbank.group.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupRequest(
        @NotBlank(message = "그룹 이름은 필수입니다.")
        @Size(max = 100, message = "그룹 이름은 100자를 초과할 수 없습니다.")
        String name,

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
        String description,

        boolean isPublic
) {}
