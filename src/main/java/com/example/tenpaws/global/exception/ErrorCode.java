package com.example.tenpaws.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    MEMBER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "Not authorized User"),
    MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "User already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Phone already exists"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid input"),

    // Admin
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "Admin not found"),
    ADMIN_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "Only authorized admin can view/update own information"),

    // Faq
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "Faq not found"),
    FAQ_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "Faq not registered"),
    FAQ_NOT_MODIFIED(HttpStatus.BAD_REQUEST, "Faq not modified"),
    FAQ_NOT_DELETED(HttpStatus.BAD_REQUEST, "Faq not deleted"),
    FAQ_NO_CHILD(HttpStatus.BAD_REQUEST, "This Faq has no children"),

    // ChatRoom
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ChatRoom not found"),
    CHATROOM_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "ChatRoom not registered"),
    CHATROOM_NOT_MODIFIED(HttpStatus.BAD_REQUEST, "ChatRoom not modified"),
    CHATROOM_NOT_DELETED(HttpStatus.BAD_REQUEST, "ChatRoom not deleted"),
    NOT_PARTICIPANT(HttpStatus.BAD_REQUEST, "You are not participant"),

    // ChatMessage
    CHAT_MESSAGE_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "ChatMessage not registered"),

    // UnReadChatMessages
    UNREAD_CHAT_MESSAGES_NOT_FOUND(HttpStatus.NOT_FOUND, "Unread ChatMessages not found"),
    UNREAD_CHAT_MESSAGES_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "Unread ChatMessages not registered"),
    UNREAD_CHAT_MESSAGES_NOT_MODIFIED(HttpStatus.BAD_REQUEST, "Unread ChatMessages not modified"),

    // Announcement
    ANNOUNCEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Announcement not found"),

    // Inquiry
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "Inquiry not found"),
    MEMBER_OR_SHELTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member or Shelter Not found"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "Unauthorized."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found"),

    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification not found"),
    NOTIFICATION_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Unsupported notification type"),
    WRITER_NOT_FOUND(HttpStatus.NOT_FOUND, "Writer not found"),

    // Shelter
    SHELTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Shelter not found with ID"),

    // Pet
    PET_NOT_FOUND(HttpStatus.NOT_FOUND,"Pet not found with ID"),
    NOT_ASSIGNED(HttpStatus.BAD_REQUEST,"The pet is not assigned to this shelter." ),
    RECOMMENDATION_FAILED(HttpStatus.NO_CONTENT, "Recommendation failed");


    private final HttpStatus httpStatus;
    private final String message;
}
