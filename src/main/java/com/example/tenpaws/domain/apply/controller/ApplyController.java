package com.example.tenpaws.domain.apply.controller;

import com.example.tenpaws.domain.apply.dto.ApplyDto;
import com.example.tenpaws.domain.apply.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applypet")
public class ApplyController {

    private final ApplyService applyService;

    // 회원의 입양 신청
    @PostMapping
    public ResponseEntity<ApplyDto> applyForPet(@RequestParam Long petId, @RequestParam Long userId) {
        ApplyDto apply = applyService.applyForPet(petId, userId);
        return ResponseEntity.ok(apply);
    }

    // 보호소 신청 목록 조회
    @GetMapping("/shelter/{shelterId}")
    public ResponseEntity<List<ApplyDto>> getAppliesForShelter(@PathVariable Long shelterId) {
        List<ApplyDto> applies = applyService.getAppliesForShelter(shelterId);
        return ResponseEntity.ok(applies);
    }

    // 신청 상태 변경
    @PutMapping("/{shelterId}/status")
    public ResponseEntity<ApplyDto> updateApplyStatus(@RequestParam Long applyId, @RequestParam String status, @PathVariable Long shelterId) {
        ApplyDto updatedApply = applyService.updateApplyStatus(applyId, status);
        return ResponseEntity.ok(updatedApply);
    }
}
