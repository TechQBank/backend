package com.qbank.user.application.dto;

import com.qbank.user.domain.User;

public record PublicUserResponse(
        Long id,
        String nickname,
        int avatarId,
        long followerCount,
        long followingCount,
        boolean isFollowing
) {
    public static PublicUserResponse of(User user, long followerCount, long followingCount, boolean isFollowing) {
        return new PublicUserResponse(
                user.getId(), user.getNickname(), user.getAvatarId(),
                followerCount, followingCount, isFollowing
        );
    }
}
