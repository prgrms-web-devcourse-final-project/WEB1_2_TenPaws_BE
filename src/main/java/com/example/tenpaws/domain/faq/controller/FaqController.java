package com.example.tenpaws.domain.faq.controller;

import com.example.tenpaws.domain.faq.dto.FaqRequest;
import com.example.tenpaws.domain.faq.dto.FaqResponse;
import com.example.tenpaws.domain.faq.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faqs")
public class FaqController {
    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FaqResponse>> getFaq() {
        return ResponseEntity.ok(faqService.read());
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<List<FaqResponse>> getChildFaq(@PathVariable("parentId") Long parentId) {
        return ResponseEntity.ok(faqService.findByParentId(parentId));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<FaqResponse> createFaq(@Valid @RequestBody FaqRequest faqRequest) {
        return ResponseEntity.ok(faqService.create(faqRequest));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PutMapping("/{faqId}")
    public ResponseEntity<FaqResponse> updateFaq(@Valid @RequestBody FaqRequest faqRequest, @PathVariable("faqId") Long faqId) {
        faqRequest.setFaqId(faqId);
        return ResponseEntity.ok(faqService.update(faqRequest));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{faqId}")
    public ResponseEntity<Map<String, String>> deleteFaq(@PathVariable("faqId") Long faqId) {
        faqService.delete(faqId);
        return ResponseEntity.ok(Map.of("message", "Faq deleted"));
    }
}
