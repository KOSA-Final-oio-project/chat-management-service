package com.oio.chatservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDto {

    // 메시지 유형 구분
    // ENTER -> 사용자가 채팅방에 입장할 때 사용
    // COMM -> 일반 메시지
    public enum MessageType {
        ENTER, TALK
    }

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String message;

} // end class
