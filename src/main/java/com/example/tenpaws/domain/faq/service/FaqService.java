package com.example.tenpaws.domain.faq.service;

import com.example.tenpaws.domain.faq.dto.FaqRequest;
import com.example.tenpaws.domain.faq.dto.FaqResponse;

import java.util.List;

public interface FaqService {
    FaqResponse create(FaqRequest faqRequest);
    FaqResponse read(Long faqId);
    FaqResponse update(FaqRequest faqRequest);
    void delete(Long faqId);
    List<FaqResponse> findByParentId(Long parentId);
}
