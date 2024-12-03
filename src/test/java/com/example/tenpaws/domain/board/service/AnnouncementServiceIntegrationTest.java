package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.admin.entity.Admin;
import com.example.tenpaws.domain.admin.repository.AdminRepository;
import com.example.tenpaws.domain.board.dto.request.AnnouncementRequest;
import com.example.tenpaws.domain.board.dto.response.AnnouncementListViewResponse;
import com.example.tenpaws.domain.board.dto.response.AnnouncementResponse;
import com.example.tenpaws.domain.board.entity.AnnouncementCategory;
import com.example.tenpaws.domain.board.repository.AnnouncementRepository;
import com.example.tenpaws.global.entity.UserRole;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AnnouncementServiceIntegrationTest {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AdminRepository adminRepository;

    private Admin admin;
    private AnnouncementRequest request;

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .username("관리자")
                .password("password123")
                .email("admin@test.com")
                .userRole(UserRole.ROLE_ADMIN)
                .build();
        adminRepository.save(admin);

        request = new AnnouncementRequest(
                AnnouncementCategory.NOTICE,
                "공지사항",
                "공지 내용"
        );
    }

    @Test
    void 공지사항_생성() {
        // when
        AnnouncementResponse response = announcementService.create(request, admin.getEmail());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("공지사항");
        assertThat(response.getCategory()).isEqualTo(AnnouncementCategory.NOTICE);
        assertThat(response.getContent()).isEqualTo("공지 내용");
        assertThat(response.getViewCount()).isEqualTo(0L);
        assertThat(response.getCreated_at()).isEqualTo(LocalDate.now());
    }

    @Test
    void 잘못된_이메일로_생성시_예외발생() {
        // assertThatThrownBy(): 예외 발생을 검증하는 AssertJ 메서드
        assertThatThrownBy(() -> announcementService.create(request, "wrong@test.com"))
                .isInstanceOf(BaseException.class)                                 // 발생한 예외의 타입 검증
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND); // 예외의 에러 코드 검증
    }

    @Test
    void 공지사항_목록_페이징_조회() {
        // given
        여러_공지사항_생성(15);
        Pageable firstPage = PageRequest.of(0, 10);
        Pageable secondPage = PageRequest.of(1, 10);

        // when
        Page<AnnouncementListViewResponse> firstPageResult = announcementService.getList(firstPage);
        Page<AnnouncementListViewResponse> secondPageResult = announcementService.getList(secondPage);

        // then
        assertThat(firstPageResult.getContent()).hasSize(10);
        assertThat(secondPageResult.getContent()).hasSize(5);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(15);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(2);
    }

    @Test
    void 공지사항_상세조회_및_조회수증가() {
        // given
        AnnouncementResponse created = announcementService.create(request, admin.getEmail());

        // when
        AnnouncementResponse response = announcementService.findById(created.getId());

        // then
        assertThat(response.getViewCount()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("공지사항");
        assertThat(response.getContent()).isEqualTo("공지 내용");
    }

    @Test
    void 공지사항_수정() {
        // given
        AnnouncementResponse created = announcementService.create(request, admin.getEmail());
        AnnouncementRequest updateRequest = new AnnouncementRequest(
                AnnouncementCategory.SUPPORT,
                "수정된 제목",
                "수정된 내용"
        );

        // when
        AnnouncementResponse updated = announcementService.update(created.getId(), updateRequest);

        // then
        assertThat(updated.getCategory()).isEqualTo(AnnouncementCategory.SUPPORT);
        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("공지사항 삭제")
    void 공지사항_삭제() {
        // given
        AnnouncementResponse created = announcementService.create(request, admin.getEmail());

        // when
        announcementService.delete(created.getId());

        // then
        assertThatThrownBy(() -> announcementService.findById(created.getId()))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ANNOUNCEMENT_NOT_FOUND);
    }

    @Test
    void 존재하지_않는_공지사항_조회시_예외발생() {
        // when & then: 존재하지 않는 ID로 조회 시 예외 발생 확인
        assertThatThrownBy(() -> announcementService.findById(999L))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ANNOUNCEMENT_NOT_FOUND);
    }

    // 공지사항 다수 생성 메서드
    private void 여러_공지사항_생성(int count) {
        for (int i = 1; i <= count; i++) {
            AnnouncementRequest testRequest = new AnnouncementRequest(
                    i % 2 == 0 ? AnnouncementCategory.NOTICE : AnnouncementCategory.SUPPORT,
                    String.format("공지사항 %d", i),
                    String.format("내용 %d", i)
            );
            announcementService.create(testRequest, admin.getEmail());
        }
    }
}