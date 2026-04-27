package com.qbank.answer.application.dto;

import com.qbank.answer.domain.UserQuestionAnswer;
import com.qbank.user.domain.User;

import java.time.LocalDateTime;
import java.util.Objects;

public record PublicAnswerResponse(
        Long id,
        String content,
        Long authorId,
        String authorNickname,
        int authorAvatarId,
        long likeCount,
        boolean isLiked,
        boolean isMyAnswer,
        LocalDateTime updatedAt
) {
    public static PublicAnswerResponse of(UserQuestionAnswer answer, User author,
                                          long likeCount, boolean isLiked, Long currentUserId) {
        return new PublicAnswerResponse(
                answer.getId(),
                answer.getContent(),
                answer.getUserId(),
                author != null ? author.getNickname() : "알 수 없음",
                author != null ? author.getAvatarId() : 0,
                likeCount,
                isLiked,
                Objects.equals(answer.getUserId(), currentUserId),
                answer.getUpdatedAt()
        );
    }
}
