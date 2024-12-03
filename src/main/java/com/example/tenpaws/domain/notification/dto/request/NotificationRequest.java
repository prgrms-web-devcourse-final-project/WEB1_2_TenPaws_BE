package com.example.tenpaws.domain.notification.dto.request;

import com.example.tenpaws.domain.notification.entity.Notification;
import com.example.tenpaws.domain.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotNull(message = "알림 타입은 필수입니다")
    private NotificationType type;

    @NotBlank(message = "알림 내용은 필수입니다")
    private String content;

    @NotBlank(message = "수신자 이메일은 필수입니다")
    private String recipientEmail;

    public Notification toEntity() {
        return Notification.builder()
                .content(content)
                .type(type)
                .recipientEmail(recipientEmail)
                .build();
    }
}
