package com.example.tenpaws.domain.board.dto.response;

import com.example.tenpaws.domain.board.entity.Inquiry;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class InquiryDetailResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String writerName;
    private final Long viewCount;
    private final LocalDate created_at;
    private final List<CommentResponse> comments;

    public InquiryDetailResponse(Inquiry inquiry, List<CommentResponse> comments) {
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.writerName = inquiry.getUser() == null ? inquiry.getShelter().getShelterName() : inquiry.getUser().getUsername();
        this.viewCount = inquiry.getViewCount();
        this.created_at = inquiry.getCreated_at();
        this.comments = comments;
    }
}