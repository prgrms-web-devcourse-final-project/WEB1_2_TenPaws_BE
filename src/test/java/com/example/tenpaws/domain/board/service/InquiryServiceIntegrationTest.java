package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.board.dto.request.InquiryRequest;
import com.example.tenpaws.domain.board.dto.response.InquiryDetailResponse;
import com.example.tenpaws.domain.board.dto.response.InquiryListViewResponse;
import com.example.tenpaws.domain.board.dto.response.InquiryResponse;
import com.example.tenpaws.domain.board.repository.InquiryRepository;
import com.example.tenpaws.domain.notification.dto.request.NotificationRequest;
import com.example.tenpaws.domain.notification.factory.NotificationFactory;
import com.example.tenpaws.domain.notification.service.NotificationService;
import com.example.tenpaws.domain.shelter.entity.Shelter;
import com.example.tenpaws.domain.shelter.repository.ShelterRepository;
import com.example.tenpaws.domain.user.entity.User;
import com.example.tenpaws.domain.user.repositoty.UserRepository;
import com.example.tenpaws.global.entity.UserRole;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import com.example.tenpaws.global.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class InquiryServiceIntegrationTest {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationFactory notificationFactory;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private User user;
    private Shelter shelter;
    private InquiryRequest request;
    private NotificationRequest mockNotificationRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        user = User.builder()
                .email("user@test.com")
                .username("테스트 유저")
                .password("password123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .userRole(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);

        // 테스트용 보호소 생성
        shelter = Shelter.builder()
                .email("shelter@test.com")
                .shelterName("테스트 보호소")
                .password("password123")
                .address("서울시 서초구")
                .phoneNumber("010-1111-3333")
                .userRole(UserRole.ROLE_SHELTER)
                .build();
        shelterRepository.save(shelter);

        // 테스트용 문의 요청 객체 생성
        request = new InquiryRequest(
                "문의 제목",
                "문의 내용"
        );

        // Mock 설정
        mockNotificationRequest = mock(NotificationRequest.class);

        // CustomUserDetailsService Mock 설정
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("role", UserRole.ROLE_USER);
        given(userDetailsService.getInfosByEmail(user.getEmail())).willReturn(userInfo);

        Map<String, Object> shelterInfo = new HashMap<>();
        shelterInfo.put("role", UserRole.ROLE_SHELTER);
        given(userDetailsService.getInfosByEmail(shelter.getEmail())).willReturn(shelterInfo);

        // NotificationFactory Mock 설정
        given(notificationFactory.createInquirySubmittedNotification(any(String.class)))
                .willReturn(mockNotificationRequest);
    }

    @Test
    void 일반_사용자_문의_생성() {
        // when
        InquiryResponse response = inquiryService.create(request, user.getEmail());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("문의 제목");
        assertThat(response.getContent()).isEqualTo("문의 내용");
        assertThat(response.getWriterName()).isEqualTo("테스트 유저");
        assertThat(response.getViewCount()).isEqualTo(0L);
        assertThat(response.getCreated_at()).isEqualTo(LocalDate.now());

        verify(notificationService).create(mockNotificationRequest);
    }

    @Test
    void 보호소_문의_생성() {
        // when
        InquiryResponse response = inquiryService.create(request, shelter.getEmail());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("문의 제목");
        assertThat(response.getContent()).isEqualTo("문의 내용");
        assertThat(response.getWriterName()).isEqualTo("테스트 보호소");
        assertThat(response.getViewCount()).isEqualTo(0L);

        verify(notificationService).create(mockNotificationRequest);
    }

    @Test
    void 잘못된_이메일로_문의_생성() {
        // given
        String wrongEmail = "wrong@test.com";
        Map<String, Object> wrongInfo = new HashMap<>();
        wrongInfo.put("role", UserRole.ROLE_USER);
        given(userDetailsService.getInfosByEmail(wrongEmail)).willReturn(wrongInfo);

        // when & then
        assertThatThrownBy(() -> inquiryService.create(request, wrongEmail))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    void 문의_목록_페이징_조회() {
        // given
        여러_문의_생성(15);
        Pageable firstPage = PageRequest.of(0, 10);
        Pageable secondPage = PageRequest.of(1, 10);

        // when
        Page<InquiryListViewResponse> firstPageResult = inquiryService.getList(firstPage);
        Page<InquiryListViewResponse> secondPageResult = inquiryService.getList(secondPage);

        // then
        assertThat(firstPageResult.getContent()).hasSize(10);
        assertThat(secondPageResult.getContent()).hasSize(5);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(15);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(2);
    }

    @Test
    void 문의_상세_조회_및_조회수_증가() {
        // given
        InquiryResponse created = inquiryService.create(request, user.getEmail());

        // when
        InquiryDetailResponse response = inquiryService.findById(created.getId());

        // then
        assertThat(response.getViewCount()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("문의 제목");
        assertThat(response.getContent()).isEqualTo("문의 내용");
        assertThat(response.getWriterName()).isEqualTo("테스트 유저");
    }

    @Test
    void 문의_수정() {
        // given
        InquiryResponse created = inquiryService.create(request, user.getEmail());
        InquiryRequest updateRequest = new InquiryRequest(
                "수정된 제목",
                "수정된 내용"
        );

        // when
        InquiryResponse updated = inquiryService.update(created.getId(), updateRequest);

        // then
        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    void 문의_삭제() {
        // given
        InquiryResponse created = inquiryService.create(request, user.getEmail());

        // when
        inquiryService.delete(created.getId());

        // then
        assertThatThrownBy(() -> inquiryService.findById(created.getId()))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_NOT_FOUND);
    }

    @Test
    void 존재하지_않는_문의_조회() {
        assertThatThrownBy(() -> inquiryService.findById(999L))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_NOT_FOUND);
    }

    private void 여러_문의_생성(int count) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("role", UserRole.ROLE_USER);
        given(userDetailsService.getInfosByEmail(user.getEmail())).willReturn(userInfo);

        for (int i = 1; i <= count; i++) {
            InquiryRequest testRequest = new InquiryRequest(
                    String.format("문의 제목 %d", i),
                    String.format("문의 내용 %d", i)
            );
            inquiryService.create(testRequest, user.getEmail());
        }
    }
}