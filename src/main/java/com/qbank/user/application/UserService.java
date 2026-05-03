package com.qbank.user.application;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.follow.domain.FollowCountProjection;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<User> users = userRepository.findByNicknameContainingIgnoreCaseAndIdNot(
                nickname, currentUserId, PageRequest.of(0, 20));
        if (users.isEmpty()) return List.of();
        return buildPublicUserResponses(users, currentUserId);
    }

    public List<PublicUserResponse> getFollowers(Long targetUserId, Long currentUserId) {
        List<Long> followerIds = followRepository.findByFolloweeId(targetUserId)
                .stream().map(f -> f.getFollowerId()).toList();
        if (followerIds.isEmpty()) return List.of();
        return buildPublicUserResponses(userRepository.findAllById(followerIds), currentUserId);
    }

    public List<PublicUserResponse> getFollowing(Long targetUserId, Long currentUserId) {
        List<Long> followeeIds = followRepository.findByFollowerId(targetUserId)
                .stream().map(f -> f.getFolloweeId()).toList();
        if (followeeIds.isEmpty()) return List.of();
        return buildPublicUserResponses(userRepository.findAllById(followeeIds), currentUserId);
    }

    private List<PublicUserResponse> buildPublicUserResponses(List<User> users, Long currentUserId) {
        List<Long> userIds = users.stream().map(User::getId).toList();

        Map<Long, Long> followerCountMap = followRepository.countByFolloweeIdIn(userIds)
                .stream().collect(Collectors.toMap(FollowCountProjection::getUserId, FollowCountProjection::getCount));
        Map<Long, Long> followingCountMap = followRepository.countByFollowerIdIn(userIds)
                .stream().collect(Collectors.toMap(FollowCountProjection::getUserId, FollowCountProjection::getCount));
        Set<Long> followingIds = currentUserId != null
                ? new HashSet<>(followRepository.findFolloweeIdsByFollowerIdAndFolloweeIdIn(currentUserId, userIds))
                : Set.of();

        return users.stream()
                .map(user -> PublicUserResponse.of(
                        user,
                        followerCountMap.getOrDefault(user.getId(), 0L),
                        followingCountMap.getOrDefault(user.getId(), 0L),
                        followingIds.contains(user.getId())
                ))
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
