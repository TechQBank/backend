package com.qbank.notification.presentation;

import com.qbank.auth.SecurityUtils;
import com.qbank.notification.application.NotificationService;
import com.qbank.notification.application.dto.NotificationResponse;
import com.qbank.notification.infrastructure.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterManager sseEmitterManager;

    // SSE 연결 수립 — 클라이언트는 이 엔드포인트를 구독하고 실시간 알림을 수신
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseEmitterManager.connect(SecurityUtils.getCurrentUserId());
    }

    @GetMapping
    public List<NotificationResponse> getNotifications() {
        return notificationService.getNotifications(SecurityUtils.getCurrentUserId());
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount() {
        long count = notificationService.getUnreadCount(SecurityUtils.getCurrentUserId());
        return Map.of("count", count);
    }

    @PatchMapping("/read")
    public void markAllRead() {
        notificationService.markAllRead(SecurityUtils.getCurrentUserId());
    }
}
