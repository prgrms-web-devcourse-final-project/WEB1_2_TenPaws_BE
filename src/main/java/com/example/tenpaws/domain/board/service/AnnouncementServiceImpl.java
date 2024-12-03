package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.admin.entity.Admin;
import com.example.tenpaws.domain.admin.repository.AdminRepository;
import com.example.tenpaws.domain.board.dto.request.AnnouncementRequest;
import com.example.tenpaws.domain.board.dto.response.AnnouncementListViewResponse;
import com.example.tenpaws.domain.board.dto.response.AnnouncementResponse;
import com.example.tenpaws.domain.board.entity.Announcement;
import com.example.tenpaws.domain.board.repository.AnnouncementRepository;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AdminRepository adminRepository;
    private final AnnouncementRepository announcementRepository;

    @Override
    @Transactional
    public AnnouncementResponse create(AnnouncementRequest request, String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        Announcement savedAnnouncement = announcementRepository.save(request.toEntity(admin));
        return new AnnouncementResponse(savedAnnouncement);
    }

    @Override
    public Page<AnnouncementListViewResponse> getList(Pageable pageable) {
        return announcementRepository.findAll(pageable)
                .map(AnnouncementListViewResponse::new);
    }

    @Override
    @Transactional
    public AnnouncementResponse findById(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BaseException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));
        announcement.incrementViewCount();
        return new AnnouncementResponse(announcement);
    }

    @Override
    @Transactional
    public AnnouncementResponse update(Long announcementId, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BaseException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));
        announcement.update(request.getCategory(), request.getTitle(), request.getContent());
        return new AnnouncementResponse(announcement);
    }

    @Override
    @Transactional
    public void delete(Long announcementId) {
        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BaseException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));
        announcementRepository.deleteById(announcementId);
    }
}