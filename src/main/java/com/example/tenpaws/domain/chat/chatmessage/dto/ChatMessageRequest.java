package com.example.tenpaws.domain.chat.chatmessage.dto;

import com.example.tenpaws.domain.chat.chatmessage.entity.ChatMessage;
import com.example.tenpaws.domain.chat.chatroom.entity.ChatRoom;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequest {
    @NotBlank
    private String message;

    @NotBlank
    private String sender;

    private String receiver;

    @NonNull
    private Long chatRoomId;

    @Builder
    public ChatMessageRequest(String message, String sender, @NonNull Long chatRoomId) {
        this.message = message;
        this.sender = sender;
        this.chatRoomId = chatRoomId;
    }

    public ChatMessage toEntity(ChatRoom chatRoom) {
        return ChatMessage.builder()
                .message(message)
                .sender(sender)
                .chatRoom(chatRoom)
                .build();
    }
}
