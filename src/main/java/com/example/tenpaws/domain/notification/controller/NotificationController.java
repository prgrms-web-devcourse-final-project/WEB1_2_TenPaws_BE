package com.example.tenpaws.domain.notification.controller;

import com.example.tenpaws.domain.notification.dto.response.NotificationResponse;
import com.example.tenpaws.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        String email = authentication.getName();
        return notificationService.subscribe(email);
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getList(
            Authentication authentication,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = authentication.getName();
        return ResponseEntity.ok(notificationService.getList(email, pageable));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Message read status updated successfully");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(notificationService.getUnreadCount(email));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> delete(@PathVariable Long notificationId) {
        notificationService.delete(notificationId);
        return ResponseEntity.ok("Notification successfully deleted");
    }
}