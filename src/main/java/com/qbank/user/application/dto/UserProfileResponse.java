package com.qbank.user.application.dto;

import com.qbank.user.domain.User;

public record UserProfileResponse(Long id, String nickname, int avatarId) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user.getId(), user.getNickname(), user.getAvatarId());
    }
}
