package com.qbank.user.application;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.user.application.dto.UpdateProfileRequest;
import com.qbank.user.application.dto.UserProfileResponse;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateProfile(request.nickname(), request.avatarId());
        return UserProfileResponse.from(user);
    }
}
