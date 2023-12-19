package com.oio.chatservice.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatDto {


    // 메시지 유형 구분
    // ENTER -> 사용자가 채팅방에 입장할 때 사용
    // TALK -> 일반 메시지
    // ALERT -> 거래하기 버튼 클릭 이후 알려주기
    // QUIT -> 웹소켓 연결이 끊어졌을 때 => 읽음표시 확인용으로 사용하기
    public enum MessageType {
        ENTER, TALK, ALERT, QUIT
    }

    private MessageType messageType;
    private String roomId;
    private String sender;
    private String roomName;
    private String message;
    private String sendDate; // 메시지 발신 시간
    private String productName;
    private String productPrice;
    private String productStatus;
    private String receiver;
    private String createDate;

    // 새로운 채팅 왔을 경우 (웹소켓 끊긴 경우 QUIT 메시지 반환 -> 이후의 메시지는 다 isRead false 처리)
    private int readCnt;

    // 채팅 삭제했을 경우 안보이게 처리
    private boolean isVisibleToSender1; // 빌리는 사람
    private boolean isVisibleToSender2; // 빌려주는 사람

    /* =============================== */

    // 클라이언트측의 시간 ChatDto에 설정하기
    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

} // end class
