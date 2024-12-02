package com.example.tenpaws.domain.chat.unread.service;

import com.example.tenpaws.domain.chat.unread.dto.UnReadChatMessagesRequest;
import com.example.tenpaws.domain.chat.unread.dto.UnReadChatMessagesResponse;

import java.util.List;

public interface UnReadChatMessagesService {
    void create(Long chatRoomId, String user1, String user2);

    void update(UnReadChatMessagesRequest unReadChatMessagesRequest);

    List<UnReadChatMessagesResponse> read(String username);
}