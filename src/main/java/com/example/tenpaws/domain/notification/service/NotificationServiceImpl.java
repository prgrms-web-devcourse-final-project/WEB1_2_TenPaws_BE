package com.example.tenpaws.domain.notification.service;

import com.example.tenpaws.domain.notification.Repository.NotificationRepository;
import com.example.tenpaws.domain.notification.dto.request.CreateNotificationRequest;
import com.example.tenpaws.domain.notification.dto.response.NotificationResponse;
import com.example.tenpaws.domain.notification.entity.Notification;
import com.example.tenpaws.domain.notification.entity.NotificationType;
import com.example.tenpaws.domain.notification.sse.SseEmitters;
import com.example.tenpaws.domain.notification.sse.UserIdentifier;
import com.example.tenpaws.global.entity.UserRole;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Override
    public SseEmitter subscribe(UserRole userRole, Long userId) {
        UserIdentifier identifier = new UserIdentifier(userRole, userId);

        // 기존 연결이 있다면 정리
        SseEmitter existingEmitter = sseEmitters.get(identifier);
        if (existingEmitter != null) {
            existingEmitter.complete();
            sseEmitters.remove(identifier);
            log.info("기존 SSE 연결 종료: {}", identifier);
        }

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        try {
            // 연결 즉시 이벤트 전송
            emitter.send(SseEmitter.event()
                    .name("SSE")
                    .data("연결되었습니다.", MediaType.APPLICATION_JSON));

            sseEmitters.add(identifier, emitter);
            log.info("새로운 SSE 연결 생성: {}", identifier);
        } catch (IOException e) {
            log.error("SSE 연결 중 오류 발생", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @Override
    @Transactional
    public NotificationResponse create(CreateNotificationRequest request) {
        Notification savedNotification = notificationRepository.save(request.toEntity());

        if (savedNotification.getType().name().equals(NotificationType.NEW_CHAT_MESSAGE.name())) {
            return new NotificationResponse(savedNotification);
        }

        // 비동기로 알림 전송 처리
        try {
            notify(request.getUserRole(), request.getUserId(), new NotificationResponse(savedNotification));
        } catch (Exception e) {
            log.error("실시간 알림 전송 실패: {}", e.getMessage());
            // 알림 전송 실패는 notification 생성 자체의 실패로 이어지지 않도록 함
        }

        return new NotificationResponse(savedNotification);
    }

    @Override
    public void notify(UserRole userRole, Long userId, NotificationResponse notification) {
        UserIdentifier identifier = new UserIdentifier(userRole, userId);

        // SseEmitters 클래스의 sendNotification 메서드 활용
        boolean sent = sseEmitters.sendNotification(identifier, notification);

        if (!sent) {
            log.warn("알림 전송 실패 - 연결을 찾을 수 없거나 전송 중 오류 발생: user={}, role={}", userId, userRole);
        }
    }

    // 알림 조회 관련 메서드
    public Page<NotificationResponse> getList(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponse::new);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.markAsRead();
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void delete(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notificationRepository.delete(notification);
    }
}