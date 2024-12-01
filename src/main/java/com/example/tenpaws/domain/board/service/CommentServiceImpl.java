package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.admin.entity.Admin;
import com.example.tenpaws.domain.admin.repository.AdminRepository;
import com.example.tenpaws.domain.board.dto.request.CreateCommentRequest;
import com.example.tenpaws.domain.board.dto.request.UpdateCommentRequest;
import com.example.tenpaws.domain.board.entity.Comment;
import com.example.tenpaws.domain.board.entity.Inquiry;
import com.example.tenpaws.domain.board.repository.CommentRepository;
import com.example.tenpaws.domain.board.repository.InquiryRepository;
import com.example.tenpaws.domain.notification.factory.NotificationFactory;
import com.example.tenpaws.domain.notification.service.NotificationService;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final InquiryRepository inquiryRepository;
    private final AdminRepository adminRepository;
    private final NotificationService notificationService;
    private final NotificationFactory notificationFactory;

    @Transactional
    public Comment create(Long inquiryId, CreateCommentRequest request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BaseException(ErrorCode.INQUIRY_NOT_FOUND));
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new BaseException(ErrorCode.ADMIN_NOT_FOUND));

        Comment comment = commentRepository.save(Comment.builder()
                .inquiry(inquiry)
                .admin(admin)
                .content(request.getContent())
                .build());

        // 문의 답변 알림 생성 및 저장
        notificationService.create(
                notificationFactory.createInquiryAnsweredNotification(
                        inquiry.getWriterId(),
                        inquiry.getWriterRole()
                )
        );
        return comment;
    }

    @Transactional
    public Comment update(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
        comment.update(request.getContent());
        return comment;
    }

    @Transactional
    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new BaseException(ErrorCode.COMMENT_NOT_FOUND);
        }
        commentRepository.deleteById(commentId);
    }
}
