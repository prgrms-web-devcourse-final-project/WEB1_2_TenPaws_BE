package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.board.dto.request.CommentRequest;
import com.example.tenpaws.domain.board.dto.response.CommentResponse;

public interface CommentService {
    CommentResponse create(Long inquiryId, CommentRequest request, String email);
    CommentResponse findById(Long commentId);
    CommentResponse update(Long commentId, CommentRequest request);
    void delete(Long commentId);
}
