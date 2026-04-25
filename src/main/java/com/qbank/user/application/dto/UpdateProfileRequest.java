package com.qbank.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(max = 100, message = "닉네임은 100자를 초과할 수 없습니다")
        String nickname,

        int avatarId
) {}
