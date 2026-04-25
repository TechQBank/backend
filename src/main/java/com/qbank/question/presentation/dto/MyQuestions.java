package com.qbank.question.presentation.dto;

import com.qbank.question.domain.Visibility;
import org.springframework.data.domain.Sort;

public class MyQuestions {

    public record Request(
            String visibility,
            String sort
    ) {
        public Visibility getVisibility() {
            return visibility != null ? Visibility.of(visibility).orElse(null) : null;
        }

        public Sort toSort() {
            return "OLDEST".equalsIgnoreCase(sort)
                    ? Sort.by("createdAt").ascending()
                    : Sort.by("createdAt").descending();
        }
    }
}
