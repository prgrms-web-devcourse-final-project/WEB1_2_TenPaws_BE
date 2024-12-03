package com.example.tenpaws.domain.notification.Repository;

import com.example.tenpaws.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email, Pageable pageable);
    Long countByRecipientEmailAndIsReadFalse(String email);
}
