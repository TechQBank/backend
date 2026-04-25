package com.qbank.question.application.dto;

import com.qbank.question.domain.CareerLevel;
import com.qbank.question.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class RegisterQuestion {

    public record Request(
            @NotBlank(message = "제목은 필수입니다")
            @Size(max = 300, message = "제목은 300자를 초과할 수 없습니다")
            String title,

            @Size(max = 5, message = "태그는 최대 5개까지 선택할 수 있습니다")
            List<Long> tagIds,

            @NotBlank(message = "공개 여부는 필수입니다")
            String visibility,

            @NotBlank(message = "경력 수준은 필수입니다")
            String careerLevel,

            @Size(max = 3000, message = "내 정리는 3000자를 초과할 수 없습니다")
            String myNotes,

            List<String> keyPoints,

            @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다")
            String memo
    ) {
        public Visibility getVisibility() {
            return visibility != null ? Visibility.of(visibility).orElse(null) : null;
        }

        public CareerLevel getCareerLevel() {
            return careerLevel != null ? CareerLevel.of(careerLevel).orElse(null) : null;
        }
    }

    public record Response(Long id) {}
}
