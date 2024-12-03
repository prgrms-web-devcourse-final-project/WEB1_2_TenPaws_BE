package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.admin.entity.Admin;
import com.example.tenpaws.domain.admin.repository.AdminRepository;
import com.example.tenpaws.domain.board.dto.request.CommentRequest;
import com.example.tenpaws.domain.board.dto.response.CommentResponse;
import com.example.tenpaws.domain.board.entity.Inquiry;
import com.example.tenpaws.domain.board.repository.CommentRepository;
import com.example.tenpaws.domain.board.repository.InquiryRepository;
import com.example.tenpaws.domain.notification.factory.NotificationFactory;
import com.example.tenpaws.domain.notification.service.NotificationService;
import com.example.tenpaws.domain.user.entity.User;
import com.example.tenpaws.domain.user.repositoty.UserRepository;
import com.example.tenpaws.global.entity.UserRole;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationFactory notificationFactory;

    private Admin admin;
    private Inquiry inquiry;
    private CommentRequest request;

    @BeforeEach
    void setUp() {
        // Admin 생성
        admin = Admin.builder()
                .username("관리자")
                .password("password123")
                .email("admin@test.com")
                .userRole(UserRole.ROLE_ADMIN)
                .build();
        adminRepository.save(admin);

        // User 생성
        User user = User.builder()
                .username("사용자")
                .email("user@test.com")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .userRole(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);

        // Inquiry 생성 (User 정보 포함)
        inquiry = Inquiry.builder()
                .title("문의 제목")
                .content("문의 내용")
                .viewCount(0L)
                .user(user)    // User 설정
                .build();
        inquiryRepository.save(inquiry);

        // CommentRequest 생성
        request = new CommentRequest("답변 내용");
    }

    @Test
    void 답변_생성() {
        // when
        CommentResponse response = commentService.create(inquiry.getId(), request, admin.getEmail());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("답변 내용");
        assertThat(response.getAdminName()).isEqualTo("관리자");
        assertThat(response.getCreated_at()).isEqualTo(LocalDate.now());

        // 알림 생성 검증
        verify(notificationService).create(any());
    }

    @Test
    void 잘못된_이메일로_생성시_예외발생() {
        assertThatThrownBy(() -> commentService.create(inquiry.getId(), request, "wrong@test.com"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
    }

    @Test
    void 존재하지_않는_문의글에_답변생성시_예외발생() {
        assertThatThrownBy(() -> commentService.create(999L, request, admin.getEmail()))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_NOT_FOUND);
    }

    @Test
    void 답변_수정() {
        // given
        CommentResponse created = commentService.create(inquiry.getId(), request, admin.getEmail());
        CommentRequest updateRequest = new CommentRequest("수정된 답변 내용");

        // when
        CommentResponse updated = commentService.update(created.getId(), updateRequest);

        // then
        assertThat(updated.getContent()).isEqualTo("수정된 답변 내용");
        assertThat(updated.getAdminName()).isEqualTo("관리자");
    }

    @Test
    void 답변_삭제() {
        // given
        CommentResponse created = commentService.create(inquiry.getId(), request, admin.getEmail());

        // when
        commentService.delete(created.getId());

        // then
        assertThatThrownBy(() -> commentService.findById(created.getId()))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    void 존재하지_않는_답변_수정시_예외발생() {
        CommentRequest updateRequest = new CommentRequest("수정된 내용");
        assertThatThrownBy(() -> commentService.update(999L, updateRequest))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    void 존재하지_않는_답변_삭제시_예외발생() {
        assertThatThrownBy(() -> commentService.delete(999L))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);
    }
}