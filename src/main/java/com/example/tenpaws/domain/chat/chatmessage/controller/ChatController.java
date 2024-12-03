package com.example.tenpaws.domain.chat.chatmessage.controller;

import com.example.tenpaws.domain.chat.chatmessage.dto.ChatMessageRequest;
import com.example.tenpaws.domain.chat.chatmessage.dto.ChatMessageResponse;
import com.example.tenpaws.domain.chat.chatmessage.service.ChatMessageService;
import com.example.tenpaws.domain.chat.unread.dto.UnReadChatMessagesRequest;
import com.example.tenpaws.domain.chat.unread.service.UnReadChatMessagesService;
import com.example.tenpaws.domain.notification.dto.request.NotificationRequest;
import com.example.tenpaws.domain.notification.dto.response.NotificationResponse;
import com.example.tenpaws.domain.notification.entity.NotificationType;
import com.example.tenpaws.domain.notification.service.NotificationService;
import com.example.tenpaws.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final UnReadChatMessagesService unReadChatMessagesService;
    private final NotificationService notificationService;
    private final CustomUserDetailsService customUserDetailsService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

    @MessageMapping("/chat/send/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, ChatMessageRequest chatMessageRequest) {
        String sender = chatMessageRequest.getSender();
        String receiver = chatMessageRequest.getReceiver();
        String destination = "/topic/chatroom/" + chatRoomId;
        Map<String, Object> senderData = customUserDetailsService.getInfosByEmail(sender);
        Map<String, Object> receiverData = customUserDetailsService.getInfosByEmail(receiver);

        chatMessageRequest.setChatRoomId(chatRoomId);
        ChatMessageResponse chatMessageResponse = chatMessageService.createChatMessage(chatMessageRequest);

        chatMessageResponse.setSenderName(senderData.get("username").toString());
        messagingTemplate.convertAndSend(
                destination,
                chatMessageResponse
        );

        if (!isUserSubscribed(receiver, destination)) {
            unReadChatMessagesService.update(
                    UnReadChatMessagesRequest.builder().chatRoomId(chatRoomId).username(receiver).unReadCount(1).build());

            NotificationResponse notificationResponse = notificationService.create(NotificationRequest.builder()
                    .content(chatMessageResponse.getSenderName() + "님이 채팅을 보내셨습니다.")
                    .type(NotificationType.NEW_CHAT_MESSAGE)
                    .recipientEmail(receiver)
                    .build());

            messagingTemplate.convertAndSendToUser(
                    receiver,
                    "/queue/notifications",
                    notificationResponse
            );

        }
    }

    private boolean isUserSubscribed(String receiver, String destination) {
        SimpUser user = userRegistry.getUser(receiver);
        if (user == null) {
            return false;
        }

        for (SimpSession session : user.getSessions()) {
            for (SimpSubscription subscription : session.getSubscriptions()) {
                if (subscription.getDestination().equals(destination)) {
                    return true;
                }
            }
        }
        return false;
    }
}
