package com.qbank.follow.application;

import com.qbank.common.exception.BusinessException;
import com.qbank.common.exception.ErrorCode;
import com.qbank.follow.application.dto.FollowResponse;
import com.qbank.follow.domain.FollowRepository;
import com.qbank.notification.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    @Transactional
    public FollowResponse follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new BusinessException(ErrorCode.FOLLOW_SELF);
        }
        boolean isNewFollow = !followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        followRepository.insertIgnore(followerId, followeeId);

        if (isNewFollow) {
            notificationService.createFollowNotification(followeeId, followerId);
        }

        long followerCount = followRepository.countByFolloweeId(followeeId);
        return new FollowResponse(followerCount, true);
    }

    @Transactional
    public FollowResponse unfollow(Long followerId, Long followeeId) {
        followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .ifPresent(f -> followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId));
        long followerCount = followRepository.countByFolloweeId(followeeId);
        return new FollowResponse(followerCount, false);
    }
}
