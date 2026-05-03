package com.qbank.notification.domain;

import com.qbank.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    public static Notification follow(Long recipientId, Long actorId) {
        Notification n = new Notification();
        n.recipientId = recipientId;
        n.actorId = actorId;
        n.type = NotificationType.FOLLOW;
        return n;
    }

    public void markRead() {
        this.isRead = true;
    }
}
