package com.qbank.question.application.dto;

import com.qbank.question.domain.CareerLevel;
import com.qbank.question.domain.Difficulty;
import com.qbank.question.domain.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionDetail {

    public record Response(
            Long id,
            Long authorId,
            String title,
            List<TagInfo> tags,
            CareerLevel careerLevel,
            Difficulty difficulty,
            Visibility visibility,
            String authorNickname,
            boolean isMyQuestion,
            boolean isBookmarked,
            long bookmarkCount,
            String myNotes,
            List<String> keyPoints,
            String memo,
            LocalDateTime createdAt
    ) {
        public record TagInfo(Long id, String name) {}
    }
}
