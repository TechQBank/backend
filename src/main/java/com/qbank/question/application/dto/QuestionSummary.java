package com.qbank.question.application.dto;

import com.qbank.question.domain.CareerLevel;
import com.qbank.question.domain.Difficulty;
import com.qbank.question.domain.Question;
import com.qbank.question.domain.Visibility;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class QuestionSummary {

    public record Request(
            String keyword,
            List<Long> tagIds,
            CareerLevel careerLevel,
            Difficulty difficulty,
            Pageable pageable,
            Long userId,
            String sort
    ) {
    }

    public record Response(
            Long id,
            String title,
            List<TagInfo> tags,
            CareerLevel careerLevel,
            Difficulty difficulty,
            Visibility visibility,
            String authorNickname,
            boolean isMyQuestion,
            boolean isBookmarked,
            Long bookmarkCount,
            LocalDateTime createdAt
    ) {
        public record TagInfo(Long id, String name) {}

        public static List<Response> of(List<Question> questions,
                                         Map<Long, List<TagInfo>> tagsByQuestionId,
                                         Map<Long, String> nicknameByAuthorId,
                                         Map<Long, Long> bookmarkCountByQuestionId,
                                         Set<Long> bookmarkedQuestionIds,
                                         Long userId) {
            return questions.stream().map(q -> new Response(
                    q.getId(),
                    q.getTitle(),
                    tagsByQuestionId.getOrDefault(q.getId(), List.of()),
                    q.getCareerLevel(),
                    q.getDifficulty(),
                    q.getVisibility(),
                    nicknameByAuthorId.getOrDefault(q.getAuthorId(), "알 수 없음"),
                    Objects.equals(q.getAuthorId(), userId),
                    bookmarkedQuestionIds.contains(q.getId()),
                    bookmarkCountByQuestionId.getOrDefault(q.getId(), 0L),
                    q.getCreatedAt()
            )).toList();
        }
    }

}
