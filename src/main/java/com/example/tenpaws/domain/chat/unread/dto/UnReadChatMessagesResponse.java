package com.example.tenpaws.domain.chat.unread.dto;

import com.example.tenpaws.domain.chat.unread.entity.UnReadChatMessages;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class UnReadChatMessagesResponse {
    @NonNull
    private Long chatRoomId;

    private int unReadCount;

    public UnReadChatMessagesResponse(UnReadChatMessages unReadChatMessages) {
        this.chatRoomId = unReadChatMessages.getChatroom().getId();
        this.unReadCount = unReadChatMessages.getUnReadCount();
    }
}
