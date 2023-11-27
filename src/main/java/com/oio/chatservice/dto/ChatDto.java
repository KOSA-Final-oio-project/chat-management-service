package com.oio.chatservice.dto;

import lombok.Data;

@Data
public class ChatDto {
    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;

}
