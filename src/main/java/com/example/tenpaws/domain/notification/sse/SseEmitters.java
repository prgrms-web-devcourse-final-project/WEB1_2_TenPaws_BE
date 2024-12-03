package com.example.tenpaws.domain.notification.sse;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitters {
    private static final Logger log = LoggerFactory.getLogger(SseEmitters.class);

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 1000;

    public SseEmitter add(String email, SseEmitter emitter) {
        // 기존 연결이 있다면 정리
        SseEmitter existingEmitter = this.emitters.get(email);
        if (existingEmitter != null) {
            existingEmitter.complete();
            this.emitters.remove(email);
            log.info("기존 SSE 연결 종료: {}", email);
        }

        this.emitters.put(email, emitter);
        log.info("새로운 SSE 연결 추가: {}", email);

        // 콜백에 로깅 추가
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(email);
            log.info("SSE 연결 타임아웃: {}", email);
        });

        emitter.onCompletion(() -> {
            this.emitters.remove(email);
            log.info("SSE 연결 완료: {}", email);
        });

        emitter.onError(e -> {
            emitter.completeWithError(e);
            this.emitters.remove(email);
            log.error("SSE 연결 에러: {}", email, e);
        });

        return emitter;
    }

    public SseEmitter get(String email) {
        return this.emitters.get(email);
    }

    public void remove(String email) {
        SseEmitter emitter = this.emitters.remove(email);
        if (emitter != null) {
            emitter.complete();
            log.info("SSE 연결 제거: {}", email);
        }
    }

    public Map<String, SseEmitter> getAll() {
        return Collections.unmodifiableMap(this.emitters);
    }

    @Scheduled(fixedRate = 45000)
    public void sendHeartbeat() {
        emitters.forEach((email, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("", MediaType.APPLICATION_JSON));
                log.debug("하트비트 전송 성공: {}", email);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(email);
                log.warn("하트비트 전송 실패로 연결 종료: {}", email);
            }
        });
    }

    public boolean sendNotification(String email, Object data) {
        SseEmitter emitter = this.emitters.get(email);

        if (emitter == null) {
            return false;
        }

        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            try {
                emitter.send(SseEmitter.event()
                        .name("알림")
                        .data(data, MediaType.APPLICATION_JSON));
                return true;
            } catch (IOException e) {
                log.warn("알림 전송 실패 (시도 {}/{}): {}", i + 1, MAX_RETRY_COUNT, email);
                if (i < MAX_RETRY_COUNT - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // 최대 재시도 횟수 초과 시 연결 종료
        emitter.complete();
        this.emitters.remove(email);
        log.error("알림 전송 최대 재시도 횟수 초과로 연결 종료: {}", email);
        return false;
    }

    @PreDestroy
    public void destroy() {
        emitters.forEach((email, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("shutdown")
                        .data("서버가 종료됩니다.", MediaType.APPLICATION_JSON));
                emitter.complete();
                log.info("서버 종료로 인한 SSE 연결 종료: {}", email);
            } catch (IOException e) {
                log.error("서버 종료 시 SSE 연결 종료 실패: {}", email);
            }
        });
        emitters.clear();
    }
}