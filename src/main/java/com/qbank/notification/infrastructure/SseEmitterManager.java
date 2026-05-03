package com.qbank.notification.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterManager {

    private static final long TIMEOUT = 30 * 60 * 1000L; // 30분
    private static final String HEARTBEAT_EVENT = "heartbeat";
    private static final String NOTIFICATION_EVENT = "notification";

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitter.onCompletion(() -> {
            log.info("SSE completed. userId={}", userId);
            emitters.remove(userId, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("SSE timeout. userId={}", userId);
            emitters.remove(userId, emitter);
        });
        emitter.onError(e -> {
            log.warn("SSE error. userId={}", userId, e);
            emitters.remove(userId, emitter);
        });

        emitters.put(userId, emitter);

        // 연결 즉시 초기 이벤트 전송 (503 방지)
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public void send(Long userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event().name(NOTIFICATION_EVENT).data(data));
        } catch (IOException e) {
            emitters.remove(userId);
        }
    }

    // 프록시/방화벽의 idle 타임아웃 방지 — 30초마다 모든 연결에 heartbeat 전송
    @Scheduled(fixedDelay = 30_000)
    public void sendHeartbeat() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name(HEARTBEAT_EVENT).data(""));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        });
    }
}
