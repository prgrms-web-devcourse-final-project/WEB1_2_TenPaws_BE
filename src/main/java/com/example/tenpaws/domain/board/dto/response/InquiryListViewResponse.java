package com.example.tenpaws.domain.board.dto.response;

import com.example.tenpaws.domain.board.entity.Inquiry;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class InquiryListViewResponse {
    private final Long id;
    private final String title;
    private final String writerName;
    private final Long viewCount;
    private final LocalDate created_at;

    public InquiryListViewResponse(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.writerName = inquiry.getUser() == null ? inquiry.getShelter().getShelterName() : inquiry.getUser().getUsername();
        this.viewCount = inquiry.getViewCount();
        this.created_at = inquiry.getCreated_at();
    }
}