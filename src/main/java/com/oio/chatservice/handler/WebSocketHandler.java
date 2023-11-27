package com.oio.chatservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oio.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private ChatRoom chatRoom;
    private String roomId;
    //웹소켓연결되었을때
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("웹소켓이 연결됨");
    }
    //양방향데이터 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //메세지의 내용을 읽어
        String payload = message.getPayload();
        log.info("{}", payload);
        //ChatMessage 타입으로 변환하고
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        //메세지에 포함된 해당룸의 UUID를 가져온다
        roomId = chatMessage.getRoomId();
        //가져온 UUID로 ChatRoom 객체를 찾고
        chatRoom = chatService.findRoomById(chatMessage.getRoomId());
        //메세지 타입에 따라 로직을 결정
        chatRoom.handlerActions(session, chatMessage, chatService);
    }
    //웹소켓 닫혔을때
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //웹소켓이 닫히면(해당 채팅방을 나가거나, 앱을 종료했을 때)
        //해당 세션을 제거
        chatRoom.getSessions().remove(session);
        //마지막남은 한명이 나가고 session count 가 0이 되면 해당 방을 제거
        chatService.deleteRoom(roomId);
        log.info("웹소켓이 닫힘");
    }
}