package com.example.tenpaws.domain.board.controller;

import com.example.tenpaws.domain.board.dto.request.CommentRequest;
import com.example.tenpaws.domain.board.dto.response.CommentResponse;
import com.example.tenpaws.domain.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inquiries/{inquiryId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long inquiryId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(commentService.create(inquiryId, request, email));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') and @ownershipVerifier.isCommentOwner(#commentId, authentication.name)")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.update(commentId, request));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') and @ownershipVerifier.isCommentOwner(#commentId, authentication.name)")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok("답변이 성공적으로 삭제되었습니다.");
    }
}