package com.oio.chatservice.dto;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
public class ChatRoomDto {

    private String roomId;
    private String name;

    //WebSocketSession은 Spring에서 Websocket Connection이 맺어진 세션
    private Set<WebSocketSession> sessions = new HashSet<>();

    /**
     * 방 생성
     * @param name
     * @return
     */
    public static ChatRoomDto createChatRoom(String name) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();

        chatRoomDto.roomId = UUID.randomUUID().toString();
        chatRoomDto.name = name;

        return chatRoomDto;
    } // createChatRoom()

} // end class