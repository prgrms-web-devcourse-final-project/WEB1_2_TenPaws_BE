package com.example.tenpaws.domain.notification.service;

import com.example.tenpaws.domain.notification.dto.request.NotificationRequest;
import com.example.tenpaws.domain.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(String email);
    void notify(String recipientEmail, NotificationResponse notification);
    NotificationResponse create(NotificationRequest request);
    Page<NotificationResponse> getList(String email, Pageable pageable);
    void markAsRead(Long notificationId);
    Long getUnreadCount(String email);
    void delete(Long notificationId);
}
