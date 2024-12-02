package com.example.tenpaws.domain.chat.chatmessage.controller;

import com.example.tenpaws.domain.chat.chatmessage.dto.ChatMessageResponse;
import com.example.tenpaws.domain.chat.chatmessage.service.ChatMessageService;
import com.example.tenpaws.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatmessages")
public class ChatMessageRestController {
    private final ChatMessageService chatMessageService;
    private final CustomUserDetailsService customUserDetailsService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SHELTER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN') and @chatRoomServiceImpl.isUserParticipated(#chatRoomId)")
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@PathVariable("chatRoomId") Long chatRoomId) {
        List<ChatMessageResponse> chatMessageResponseList = chatMessageService.getChatMessagesByChatRoomId(chatRoomId);
        List<ChatMessageResponse> list = chatMessageResponseList.stream().peek(chatMessageResponse -> {
            Map<String, Object> userDetails = customUserDetailsService.getInfosByEmail(chatMessageResponse.getSenderEmail());
            chatMessageResponse.setSenderName(userDetails.get("username").toString());
        }).toList();
        return ResponseEntity.ok(list);
    }
}
