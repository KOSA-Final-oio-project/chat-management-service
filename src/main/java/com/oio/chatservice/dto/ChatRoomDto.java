package com.oio.chatservice.dto;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
public class ChatRoomDto {

    private String roomId;
    private String name;

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