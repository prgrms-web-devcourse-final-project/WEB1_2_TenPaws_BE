package com.example.tenpaws.domain.chat.unread.service;

import com.example.tenpaws.domain.chat.chatroom.entity.ChatRoom;
import com.example.tenpaws.domain.chat.chatroom.repository.ChatRoomRepository;
import com.example.tenpaws.domain.chat.unread.dto.UnReadChatMessagesRequest;
import com.example.tenpaws.domain.chat.unread.dto.UnReadChatMessagesResponse;
import com.example.tenpaws.domain.chat.unread.entity.UnReadChatMessages;
import com.example.tenpaws.domain.chat.unread.repository.UnReadChatMessagesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UnReadChatMessagesServiceTests {
    @Autowired
    private UnReadChatMessagesService unReadChatMessagesService;
    @Autowired
    private UnReadChatMessagesRepository unReadChatMessagesRepository;

    Long savedId;

    @BeforeEach
    void setUp(@Autowired ChatRoomRepository chatRoomRepository, @Autowired UnReadChatMessagesRepository unReadChatMessagesRepository) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .user1("user1")
                .user2("user2")
                .build());
        savedId = chatRoom.getId();
        unReadChatMessagesRepository.save(UnReadChatMessages.builder()
                .chatroom(chatRoom)
                .username("user1")
                .build());
    }

    @AfterEach
    void tearDown(@Autowired ChatRoomRepository chatRoomRepository) {
        chatRoomRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreate() {
        unReadChatMessagesService.create(savedId, "user1", "user2");

        assertEquals(2, unReadChatMessagesRepository.count());
    }

    @Test
    @Transactional
    void testUpdate() {
        UnReadChatMessagesRequest unReadChatMessagesRequest = UnReadChatMessagesRequest.builder()
                .chatRoomId(savedId)
                .username("user1")
                .unReadCount(1)
                .build();

        unReadChatMessagesService.update(unReadChatMessagesRequest);

        assertEquals(1, unReadChatMessagesRepository.findAll().get(0).getUnReadCount());
    }

    @Test
    void testRead() {
        String username = "user1";

        List<UnReadChatMessagesResponse> unReadChatMessagesResponseList = unReadChatMessagesService.read(username);

        assertEquals(1, unReadChatMessagesResponseList.size());
    }
}
