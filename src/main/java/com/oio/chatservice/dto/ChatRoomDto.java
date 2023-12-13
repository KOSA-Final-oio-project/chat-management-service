package com.oio.chatservice.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Slf4j
@ToString
@Getter
@Setter
public class ChatRoomDto {

    private String roomId;
    private String name;
    private String createDate;
    private String email;

    /**
     * 방 생성
     * @param name
     * @return
     */
    public static ChatRoomDto createChatRoom(String name) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();

        chatRoomDto.roomId = UUID.randomUUID().toString();
        chatRoomDto.name = name;
        chatRoomDto.createDate
                = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> createChatRoom() invoked. " + chatRoomDto.getCreateDate());

        return chatRoomDto;
    } // createChatRoom()

} // end class