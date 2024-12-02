package com.example.tenpaws.domain.notification.entity;

public enum NotificationType {
    ADOPTION_APPLICATION_SUBMITTED,    // 입양 신청 완료 (사용자 -> 사용자)
    ADOPTION_APPLICATION_RECEIVED,     // 입양 신청 접수 (사용자 -> 보호소)
    ADOPTION_APPLICATION_RESULT,       // 입양 신청 결과 (보호소 -> 사용자)
    INQUIRY_SUBMITTED,                // 문의 등록 (사용자/보호소 -> 관리자)
    INQUIRY_ANSWERED,                 // 문의 답변 (관리자 -> 사용자/보호소)
    NEW_CHAT_MESSAGE                  // 채팅 메시지 도착
}