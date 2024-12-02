package com.example.tenpaws.domain.faq.dto;

import com.example.tenpaws.domain.faq.entity.Faq;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FaqResponse {
    @NotBlank
    private Long faqId;

    @NotBlank
    private String content;

    private Long parentId;

    public FaqResponse(Faq faq) {
        this.faqId = faq.getId();
        this.content = faq.getContent();
        this.parentId = faq.getParent() == null ? null : faq.getParent().getId();
    }
}
