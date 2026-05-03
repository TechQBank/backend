package com.qbank.notification.application.dto;

import com.qbank.notification.domain.Notification;
import com.qbank.notification.domain.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        Long actorId,
        String actorNickname,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse of(Notification n, String actorNickname) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getActorId(),
                actorNickname,
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
