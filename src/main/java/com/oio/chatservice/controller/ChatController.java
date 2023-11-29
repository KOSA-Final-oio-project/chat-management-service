package com.oio.chatservice.controller;

import com.oio.chatservice.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final SimpMessageSendingOperations smso;

    /**
     * 채팅 메시지를 처리하고, 해당 채팅방 구독자들에게 메시지를 전송
     * @param chatDto
     * 채팅 메시지의 종류(입장, 퇴장, 일반 메시지 등)에 따라 적절한 메시지 처리를 수행
     * 예를 들어, 사용자가 채팅방에 입장하는 경우, 해당 사용자의 입장 메시지를 생성해줌.
     * 이후, STOMP 메시지 브로커를 통해 해당 채팅방을 구독하고 있는 클라이언트들에게 메시지를 전송하는 역할을 수행함
     */
    @MessageMapping("/chat/message")
    public void message(ChatDto chatDto) {
        if (ChatDto.MessageType.ENTER.equals(chatDto.getMessageType())) {
            chatDto.setMessage(chatDto.getSender() + "님이 입장하셨습니다 :-) ");
        } // if

        smso.convertAndSend("/sub/chat/room/" + chatDto.getRoomId(), chatDto);
    } // message()

} // end class