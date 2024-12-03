package com.example.tenpaws.domain.board.service;

import com.example.tenpaws.domain.board.dto.request.InquiryRequest;
import com.example.tenpaws.domain.board.dto.response.InquiryDetailResponse;
import com.example.tenpaws.domain.board.dto.response.InquiryListViewResponse;
import com.example.tenpaws.domain.board.dto.response.InquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {
    InquiryResponse create(InquiryRequest request, String email);
    Page<InquiryListViewResponse> getList(Pageable pageable);
    InquiryDetailResponse findById(Long inquiryId);
    InquiryResponse update(Long inquiryId, InquiryRequest request);
    void delete(Long inquiryId);
}