package com.example.tenpaws.domain.board.controller;

import com.example.tenpaws.domain.board.dto.request.InquiryRequest;
import com.example.tenpaws.domain.board.dto.response.InquiryDetailResponse;
import com.example.tenpaws.domain.board.dto.response.InquiryListViewResponse;
import com.example.tenpaws.domain.board.dto.response.InquiryResponse;
import com.example.tenpaws.domain.board.service.InquiryService;
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
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @PreAuthorize("hasAnyRole('USER', 'SHELTER')")
    @PostMapping
    public ResponseEntity<InquiryResponse> create(
            @RequestBody InquiryRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(inquiryService.create(request, email));
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<Page<InquiryListViewResponse>> getList(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(inquiryService.getList(pageable));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryDetailResponse> findById(@PathVariable Long inquiryId) {
        return ResponseEntity.ok(inquiryService.findById(inquiryId));
    }

    @PreAuthorize("hasAnyRole('USER', 'SHELTER') and @ownershipVerifier.isInquiryOwner(#inquiryId, authentication.name)")
    @PutMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponse> update(
            @PathVariable Long inquiryId,
            @RequestBody InquiryRequest request) {
        return ResponseEntity.ok(inquiryService.update(inquiryId, request));
    }

    @PreAuthorize("hasAnyRole('USER', 'SHELTER') and @ownershipVerifier.isInquiryOwner(#inquiryId, authentication.name)")
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<String> delete(@PathVariable Long inquiryId) {
        inquiryService.delete(inquiryId);
        return ResponseEntity.ok("문의글이 성공적으로 삭제되었습니다.");
    }
}