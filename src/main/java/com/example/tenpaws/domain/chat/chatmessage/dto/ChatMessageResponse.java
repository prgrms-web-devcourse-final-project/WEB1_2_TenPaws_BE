package com.example.tenpaws.domain.chat.chatmessage.dto;

import com.example.tenpaws.domain.chat.chatmessage.entity.ChatMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageResponse {
    @NotBlank
    private String message;

    @NonNull
    private LocalDateTime chatDate;

    @NotBlank
    private String senderEmail;

    @Setter
    private String senderName;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.message = chatMessage.getMessage();
        this.chatDate = chatMessage.getChatDate();
        this.senderEmail = chatMessage.getSender();
    }
}
