package com.oio.chatservice.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatDto {

    @Builder
    public ChatDto(MessageType messageType, String roomId, String sender, String message) {
        this.messageType = messageType;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }

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

    // 삭제 시 보이는지 안보이는지 구분
    private boolean isVisibleToSender;  // 보낸 사람에게 보이는지 여부
    private boolean isVisibleToReceiver; // 받는 사람에게 보이는지 여부

} // end class
