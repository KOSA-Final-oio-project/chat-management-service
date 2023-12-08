package com.oio.chatservice.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChatDto {

    // 메시지 유형 구분
    // ENTER -> 사용자가 채팅방에 입장할 때 사용
    // TALK -> 일반 메시지
    // ALERT -> 거래하기 버튼 클릭 이후 알려주기
    public enum MessageType {
        ENTER, TALK, ALERT
    }

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String message;
    private String sendDate; // 메시지 발신 시간
    // 년월일 + 시분초 -> 클라이언트 측에서 시분초만 보여주기

    // 클라이언트측의 시간 ChatDto에 설정하기
    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    // 삭제 시 보이는지 안보이는지 구분
//    private boolean isVisibleToSender;  // 보낸 사람에게 보이는지 여부
//    private boolean isVisibleToReceiver; // 받는 사람에게 보이는지 여부

} // end class
