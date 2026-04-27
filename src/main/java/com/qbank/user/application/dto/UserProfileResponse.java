package com.qbank.user.application.dto;

import com.qbank.user.domain.User;

public record UserProfileResponse(Long id, String nickname, int avatarId, long followerCount, long followingCount) {

    public static UserProfileResponse of(User user, long followerCount, long followingCount) {
        return new UserProfileResponse(
                user.getId(), user.getNickname(), user.getAvatarId(),
                followerCount, followingCount
        );
    }
}
