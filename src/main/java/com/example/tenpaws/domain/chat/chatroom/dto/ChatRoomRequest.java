package com.example.tenpaws.domain.chat.chatroom.dto;

import com.example.tenpaws.domain.chat.chatroom.entity.ChatRoom;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomRequest {
    @NotBlank
    private String user1;

    @NotBlank
    private String user2;

    @Builder
    public ChatRoomRequest(String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .user1(user1)
                .user2(user2)
                .build();
    }
}
