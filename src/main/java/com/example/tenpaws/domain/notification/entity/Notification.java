package com.example.tenpaws.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "Notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String recipientEmail;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Boolean isRead;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Notification(String content, NotificationType type, String recipientEmail) {
        this.content = content;
        this.type = type;
        this.recipientEmail = recipientEmail;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}