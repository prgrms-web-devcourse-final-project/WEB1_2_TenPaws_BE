package com.example.tenpaws.domain.board.controller;

import com.example.tenpaws.domain.board.dto.request.CreateCommentRequest;
import com.example.tenpaws.domain.board.dto.request.UpdateCommentRequest;
import com.example.tenpaws.domain.board.dto.response.CommentResponse;
import com.example.tenpaws.domain.board.entity.Comment;
import com.example.tenpaws.domain.board.service.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inquiries/{inquiryId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceImpl commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long inquiryId,
            @RequestBody CreateCommentRequest request) {
        Comment comment = commentService.create(inquiryId, request);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {
        Comment comment = commentService.update(commentId, request);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> delete(
            @PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok("답변이 성공적으로 삭제되었습니다.");
    }
}