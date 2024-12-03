package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.board.dto.request.AnnouncementRequest;
import com.example.tenpaws.domain.board.dto.response.AnnouncementListViewResponse;
import com.example.tenpaws.domain.board.dto.response.AnnouncementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {
    AnnouncementResponse create(AnnouncementRequest request, String email);
    Page<AnnouncementListViewResponse> getList(Pageable pageable);
    AnnouncementResponse findById(Long announcementId);
    AnnouncementResponse update(Long announcementId, AnnouncementRequest request);
    void delete(Long announcementId);
}