package com.oio.chatservice.dto;

import com.oio.chatservice.service.ChatService;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ChatRoomDto {

//    private Long id;

    private String roomId;

    // pub/sub 방식 사용 -> 구독자 관리가 알아서 되기 때문에 웹소켓 세션 관리 필요 XX
    private Set<WebSocketSession> sessions = new HashSet<>(); // 채팅방에 연결된 WebSocket 세션들을 저장(HashSet = 중복 X)

    @Builder
    public ChatRoomDto(String roomId) {
        this.roomId = roomId;
    }

    /**
     * 채팅방에서 발생하는 액션 처리용 메소드
     * @param session
     * @param chatDto
     * @param chatService
     */
    public void handleActions(WebSocketSession session, ChatDto chatDto, ChatService chatService) {

        // 사용자 입장 처리
        // 사용자 입장 이후 세션을 sessions에 추가 -> 입장 메시지를 설정해서 전체 채팅방에 메시지 전송
        if(chatDto.getMessageType().equals(ChatDto.MessageType.ENTER)) {
            sessions.add(session);
            chatDto.setMessage(chatDto.getSender() + "님이 입장함.");
        }

        // 일단 모든 참여자에게 보내는 걸로 설정
        sendMessage(chatDto, chatService);
    }

    /**
     * 채팅방에 있는 모든 세션에 메시지 전송
     * @param message
     * @param chatService
     * @param <T>
     */
    public <T> void sendMessage(T message, ChatService chatService) {
        // parallelStream() = 병렬 처리
        // forEach = 채팅방에 있는 모든 세션에 동시에 메시지를 전송
        sessions.parallelStream().forEach(session -> chatService.sendMessage(session, message));
        // messageService.sendMessage(session, message)를 호출, 각 세션에 메시지를 전송

    } // sendMessage()

} // end class
