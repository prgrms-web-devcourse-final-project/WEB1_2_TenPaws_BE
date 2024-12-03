package com.example.tenpaws.global.security.verifier;

import com.example.tenpaws.domain.board.repository.AnnouncementRepository;
import com.example.tenpaws.domain.board.repository.CommentRepository;
import com.example.tenpaws.domain.board.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ownershipVerifier")
@RequiredArgsConstructor
public class OwnershipVerifier {
    private final AnnouncementRepository announcementRepository;
    private final InquiryRepository inquiryRepository;
    private final CommentRepository commentRepository;

    public boolean isAnnouncementOwner(Long id, String email) {
        return announcementRepository.findById(id)
                .map(announcement -> announcement.getAdmin().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isCommentOwner(Long id, String email) {
        return commentRepository.findById(id)
                .map(comment -> comment.getAdmin().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isInquiryOwner(Long id, String email) {
        return inquiryRepository.findById(id)
                .map(inquiry -> {
                    if (inquiry.getUser() != null) {
                        return inquiry.getUser().getEmail().equals(email);
                    }
                    if (inquiry.getShelter() != null) {
                        return inquiry.getShelter().getEmail().equals(email);
                    }
                    return false;
                })
                .orElse(false);
    }
}
