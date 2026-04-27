package com.qbank.tag.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTagRequest(
        @NotBlank(message = "태그 이름은 1자 이상 30자 이하여야 합니다.")
        @Size(max = 30, message = "태그 이름은 1자 이상 30자 이하여야 합니다.")
        String name
) {}
