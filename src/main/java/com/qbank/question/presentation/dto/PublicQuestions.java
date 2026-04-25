package com.qbank.question.presentation.dto;

import com.qbank.question.application.dto.QuestionSummary;
import com.qbank.question.domain.CareerLevel;
import com.qbank.question.domain.Difficulty;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public class PublicQuestions {

    public record Request(
            String keyword,
            List<Long> tagIds,
            String careerLevel,
            String difficulty
    ) {

        public CareerLevel getCareerLevel() {
            return careerLevel != null ? CareerLevel.of(careerLevel).orElse(null) : null;
        }

        public Difficulty getDifficulty() {
            return difficulty != null ? Difficulty.of(difficulty).orElse(null) : null;
        }

        public QuestionSummary.Request to(Pageable pageable, Long userId) {
            return new QuestionSummary.Request(keyword, tagIds, getCareerLevel(), getDifficulty(), pageable, userId);
        }
    }

    public record Response(
            Long id,
            String title,
            List<TagInfo> tags,
            String careerLevel,
            String difficulty,
            String visibility,
            String authorNickname,
            boolean isMyQuestion,
            boolean isBookmarked,
            Long bookmarkCount,
            LocalDateTime createdAt

    ) {
        public record TagInfo(Long id, String name) {}

        public static Response of(QuestionSummary.Response dto) {

            return new Response(
                    dto.id(),
                    dto.title(),
                    dto.tags().stream().map(tagInfo -> new TagInfo(tagInfo.id(), tagInfo.name())).toList(),
                    dto.careerLevel() != null ? dto.careerLevel().name() : null,
                    dto.difficulty() != null ? dto.difficulty().name() : null,
                    dto.visibility() != null ? dto.visibility().name() : null,
                    dto.authorNickname(),
                    dto.isMyQuestion(),
                    dto.isBookmarked(),
                    dto.bookmarkCount(),
                    dto.createdAt()
            );
        }
    }
}
