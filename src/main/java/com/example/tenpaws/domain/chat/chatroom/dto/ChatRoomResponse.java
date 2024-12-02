package com.example.tenpaws.domain.chat.chatroom.dto;

import com.example.tenpaws.domain.chat.chatroom.entity.ChatRoom;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomResponse {
    private Long chatRoomId;

    @NotBlank
    private String user1;

    @NotBlank
    private String user2;

    private int unReadCount;

    public ChatRoomResponse(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getId();
        this.user1 = chatRoom.getUser1();
        this.user2 = chatRoom.getUser2();
    }
}
