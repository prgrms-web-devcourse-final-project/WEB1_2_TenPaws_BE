package com.example.tenpaws.domain.board.controller;

import com.example.tenpaws.domain.board.dto.request.AnnouncementRequest;
import com.example.tenpaws.domain.board.dto.response.AnnouncementListViewResponse;
import com.example.tenpaws.domain.board.dto.response.AnnouncementResponse;
import com.example.tenpaws.domain.board.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<AnnouncementResponse> create(
            @RequestBody AnnouncementRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(announcementService.create(request, email));
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<Page<AnnouncementListViewResponse>> getList(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(announcementService.getList(pageable));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{announcementId}")
    public ResponseEntity<AnnouncementResponse> findById(@PathVariable Long announcementId) {
        return ResponseEntity.ok(announcementService.findById(announcementId));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') and @ownershipVerifier.isAnnouncementOwner(#announcementId, authentication.name)")
    @PutMapping("/{announcementId}")
    public ResponseEntity<AnnouncementResponse> update(
            @PathVariable Long announcementId,
            @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.update(announcementId, request));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') and @ownershipVerifier.isAnnouncementOwner(#announcementId, authentication.name)")
    @DeleteMapping("/{announcementId}")
    public ResponseEntity<String> delete(@PathVariable Long announcementId) {
        announcementService.delete(announcementId);
        return ResponseEntity.ok("Announcement successfully deleted");
    }
}