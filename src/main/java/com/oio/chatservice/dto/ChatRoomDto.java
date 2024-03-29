//package com.oio.chatservice.dto;
//
//import lombok.*;
//import lombok.extern.slf4j.Slf4j;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.UUID;
//
//
//@Slf4j
//@ToString
//@RequiredArgsConstructor
//@Getter
//@Setter
//public class ChatRoomDto {
//
//    private String roomId;
//    private String name;
//    private String createDate;
//    private String email;
//
//    /**
//     * 방 생성
//     * @param name
//     * @return
//     */
//    public static ChatRoomDto createChatRoom(String name) {
//        ChatRoomDto chatRoomDto = new ChatRoomDto();
//
//        chatRoomDto.roomId = UUID.randomUUID().toString();
//        chatRoomDto.name = name;
//        chatRoomDto.createDate
//                = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> createChatRoom() invoked. " + chatRoomDto.getRoomId());
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> createChatRoom() invoked. " + chatRoomDto.getName());
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> createChatRoom() invoked. " + chatRoomDto.getCreateDate());
//
//        return chatRoomDto;
//    } // createChatRoom()
//
//} // end class

package com.oio.chatservice.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@ToString
@RequiredArgsConstructor
@Getter
@Setter
public class ChatRoomDto {

    private String roomId;
    private String roomName;
    private String createDate;
    private String sender;
    private String productNo;
    private String productName;
    private String productPrice;
    private String receiver;

    /**
     * 방 생성
     *
     * @param roomName      채팅방 이름
     * @param productName   제품 이름
     * @param productPrice  제품 가격
     * @param sender 발신자 닉네임
     * @param receiver 수신자 닉네임
     * @return 생성된 채팅방 정보
     */
    public static ChatRoomDto createChatRoom(String roomName, String productNo, String productName, String productPrice, String sender, String receiver) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();

        log.info(sender);

        chatRoomDto.roomId = UUID.randomUUID().toString();
        chatRoomDto.roomName = roomName;
        chatRoomDto.productNo = productNo;
        chatRoomDto.productName = productName;
        chatRoomDto.productPrice = productPrice;
        chatRoomDto.receiver = receiver;
        chatRoomDto.sender = sender;
        chatRoomDto.createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ChatRoom created: {}", chatRoomDto);

        return chatRoomDto;
    } // createChatRoom()

} // end class
