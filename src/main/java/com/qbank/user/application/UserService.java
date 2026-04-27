package com.qbank.user.application;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.follow.domain.FollowRepository;
import com.qbank.user.application.dto.PublicUserResponse;
import com.qbank.user.application.dto.UpdateProfileRequest;
import com.qbank.user.application.dto.UserProfileResponse;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        long followerCount = followRepository.countByFolloweeId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        return UserProfileResponse.of(user, followerCount, followingCount);
    }

    public PublicUserResponse getUserProfile(Long targetUserId, Long currentUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        long followerCount = followRepository.countByFolloweeId(targetUserId);
        long followingCount = followRepository.countByFollowerId(targetUserId);
        boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, targetUserId);
        return PublicUserResponse.of(user, followerCount, followingCount, isFollowing);
    }

    public List<PublicUserResponse> searchUsers(String nickname, Long currentUserId) {
        return userRepository.findByNicknameContainingIgnoreCaseAndIdNot(
                        nickname, currentUserId, PageRequest.of(0, 20))
                .stream()
                .map(user -> {
                    long followerCount = followRepository.countByFolloweeId(user.getId());
                    long followingCount = followRepository.countByFollowerId(user.getId());
                    boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, user.getId());
                    return PublicUserResponse.of(user, followerCount, followingCount, isFollowing);
                })
                .toList();
    }

    public List<PublicUserResponse> getFollowers(Long targetUserId, Long currentUserId) {
        List<Long> followerIds = followRepository.findByFolloweeId(targetUserId)
                .stream().map(f -> f.getFollowerId()).toList();
        if (followerIds.isEmpty()) return List.of();
        return buildPublicUserResponses(followerIds, currentUserId);
    }

    public List<PublicUserResponse> getFollowing(Long targetUserId, Long currentUserId) {
        List<Long> followeeIds = followRepository.findByFollowerId(targetUserId)
                .stream().map(f -> f.getFolloweeId()).toList();
        if (followeeIds.isEmpty()) return List.of();
        return buildPublicUserResponses(followeeIds, currentUserId);
    }

    private List<PublicUserResponse> buildPublicUserResponses(List<Long> userIds, Long currentUserId) {
        return userRepository.findAllById(userIds)
                .stream()
                .map(user -> {
                    long followerCount = followRepository.countByFolloweeId(user.getId());
                    long followingCount = followRepository.countByFollowerId(user.getId());
                    boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, user.getId());
                    return PublicUserResponse.of(user, followerCount, followingCount, isFollowing);
                })
                .toList();
    }

    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateProfile(request.nickname(), request.avatarId());
        long followerCount = followRepository.countByFolloweeId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        return UserProfileResponse.of(user, followerCount, followingCount);
    }
}
