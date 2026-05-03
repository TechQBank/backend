package com.qbank.notification.application;

import com.qbank.notification.application.dto.NotificationResponse;
import com.qbank.notification.domain.Notification;
import com.qbank.notification.domain.NotificationRepository;
import com.qbank.notification.infrastructure.SseEmitterManager;
import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SseEmitterManager sseEmitterManager;

    @Transactional
    public void createFollowNotification(Long recipientId, Long actorId) {
        Notification notification = Notification.follow(recipientId, actorId);
        notificationRepository.save(notification);

        String actorNickname = userRepository.findById(actorId)
                .map(User::getNickname)
                .orElse("알 수 없음");

        sseEmitterManager.send(recipientId, NotificationResponse.of(notification, actorNickname));
    }

    public List<NotificationResponse> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findTop20ByRecipientIdOrderByCreatedAtDesc(userId);

        Set<Long> actorIds = notifications.stream()
                .map(Notification::getActorId)
                .collect(Collectors.toSet());

        Map<Long, String> nicknameMap = userRepository.findAllById(actorIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        return notifications.stream()
                .map(n -> NotificationResponse.of(n, nicknameMap.getOrDefault(n.getActorId(), "알 수 없음")))
                .toList();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllReadByRecipientId(userId);
    }
}
