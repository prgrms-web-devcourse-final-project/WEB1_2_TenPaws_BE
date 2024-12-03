package com.example.tenpaws.domain.notification.service;

import com.example.tenpaws.domain.notification.Repository.NotificationRepository;
import com.example.tenpaws.domain.notification.dto.request.NotificationRequest;
import com.example.tenpaws.domain.notification.dto.response.NotificationResponse;
import com.example.tenpaws.domain.notification.entity.Notification;
import com.example.tenpaws.domain.notification.entity.NotificationType;
import com.example.tenpaws.domain.notification.sse.SseEmitters;
import com.example.tenpaws.domain.user.entity.User;
import com.example.tenpaws.domain.user.repositoty.UserRepository;
import com.example.tenpaws.global.entity.UserRole;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SseEmitters sseEmitters;

    private User testUser;
    private NotificationRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .username("테스트 사용자")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .userRole(UserRole.ROLE_USER)
                .build();
        userRepository.save(testUser);

        testRequest = NotificationRequest.builder()
                .type(NotificationType.ADOPTION_APPLICATION_SUBMITTED)
                .content("테스트 알림 내용")
                .recipientEmail(testUser.getEmail())
                .build();
    }

    @Test
    void SSE_구독_성공_테스트() throws InterruptedException {
        // given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        // when
        SseEmitter emitter = notificationService.subscribe(testUser.getEmail());

        // then
        assertThat(emitter).isNotNull();
        assertThat(sseEmitters.get(testUser.getEmail())).isNotNull();

        latch.await(1, TimeUnit.SECONDS);
        assertThat(sseEmitters.get(testUser.getEmail())).isNotNull();
    }

    @Test
    void 알림_생성_및_삭제_테스트() {
        // when
        NotificationResponse response = notificationService.create(testRequest);

        // then
        Notification savedNotification = notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(testUser.getEmail(), PageRequest.of(0, 1))
                .getContent()
                .get(0);

        assertThat(savedNotification.getContent()).isEqualTo("테스트 알림 내용");
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.ADOPTION_APPLICATION_SUBMITTED);

        // when
        notificationService.delete(savedNotification.getId());

        // then
        assertThat(notificationRepository.findById(savedNotification.getId())).isEmpty();
    }

    @Test
    void 알림_읽음_처리_테스트() {
        // given
        notificationService.create(testRequest);
        Notification notification = notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(testUser.getEmail(), PageRequest.of(0, 1))
                .getContent()
                .get(0);

        // when
        notificationService.markAsRead(notification.getId());

        // then
        Notification updatedNotification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updatedNotification.getIsRead()).isTrue();
    }

    @Test
    void 읽지_않은_알림_개수_조회_테스트() {
        // given
        createMultipleNotifications(3);

        // when
        Long unreadCount = notificationService.getUnreadCount(testUser.getEmail());

        // then
        assertThat(unreadCount).isEqualTo(3L);
    }

    @Test
    void 존재하지_않는_수신자에게_알림_생성_예외_테스트() {
        // given
        NotificationRequest invalidRequest = NotificationRequest.builder()
                .type(NotificationType.ADOPTION_APPLICATION_SUBMITTED)
                .content("테스트 알림")
                .recipientEmail("invalid@example.com")
                .build();

        // when & then
        assertThatThrownBy(() -> notificationService.create(invalidRequest))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_OR_SHELTER_NOT_FOUND);
    }

    private void createMultipleNotifications(int count) {
        for (int i = 0; i < count; i++) {
            NotificationRequest request = NotificationRequest.builder()
                    .type(NotificationType.ADOPTION_APPLICATION_SUBMITTED)
                    .content("테스트 알림 " + (i + 1))
                    .recipientEmail(testUser.getEmail())
                    .build();
            notificationService.create(request);
        }
    }
}
