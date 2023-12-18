package com.oio.chatservice.controller;

import com.oio.chatservice.dto.ChatDto;
import com.oio.chatservice.dto.ChatRoomDto;
import com.oio.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat") // 모든 오리진과 헤더를 허용
public class ChatRoomController {

    private final ChatService chatService;

    /* ------------------------------------------------------------------------------------ */

    /**
     * 채팅방 생성
     * @param roomName
     * @param createDate
     * @param productName
     * @param productPrice
     * @param productStatus
     * @param receiver
     * @param sender
     * @return 생성된 채팅방 정보
     */
    @PostMapping("/room/{roomName}/{createDate}/{productName}/{productPrice}/{productStatus}/{receiver}/{sender}")
//    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto createChatRoom (
            @PathVariable String roomName,
            @PathVariable String createDate,
            @PathVariable String productName,
            @PathVariable String productPrice,
            @PathVariable String productStatus,
            @PathVariable String receiver,
            @PathVariable String sender) {
//            @RequestBody ChatRoomDto chatRoomDto) {

        // ChatRoomDto 객체 생성
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setRoomId(UUID.randomUUID().toString());
        chatRoomDto.setRoomName(roomName);
        chatRoomDto.setCreateDate(createDate); // 또는 서버 시간 사용
        chatRoomDto.setProductName(productName);
        chatRoomDto.setProductPrice(productPrice);
        chatRoomDto.setProductStatus(productStatus);
        chatRoomDto.setReceiver(receiver);
        chatRoomDto.setSender(sender);
//

//        log.info("생성 요청된 채팅방 정보: {}", chatRoomDto);
        return chatService.createChatRoom(chatRoomDto);

    }

    /* ------------------------------------------------------------------------------------ */

    /**
     * 모든 채팅방 목록 반환
     * @return 채팅방 목록 데이터
     */
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> chatRoom() {
        log.info(">>>>>>>>>>>>>>>>> chatRoom() invoked");

        return chatService.findAllChatRoom();
    } // chatRoom()

    /* ------------------------------------------------------------------------------------ */

    /**
     * email에 해당하는 채팅방 목록 반환
     * @return 채팅방 목록 데이터
     */
    @GetMapping("/rooms/{email}")
    @ResponseBody
    public List<ChatRoomDto> chatRoomByEmail(@PathVariable String email) throws IOException {
        log.info(">>>>>>>>>>>>>>>>> chatRoomByEmail() invoked");
        log.info(">>>>>>>>>>>>>>>>> 전달받은 email: " + email);

        chatService.loadChatRooms(email); // 사용자별 채팅방 목록 로드
//        chatService.loadChatRooms(); // 사용자별 채팅방 목록 로드

        return chatService.findChatRoomByEmail(email);
    } // chatRoom()

    /**
     * 특정 채팅방 조회
     * @param roomId 조회할 채팅방의 ID
     * @return 조회된 채팅방 정보
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto chatRoomInfo(@PathVariable String roomId) {
        log.info(">>>>>>>>>>>>>>>>> chatRoomInfo() invoked");

        return chatService.findChatRoomById(roomId);
    } // chatRoomInfo()

    /* ------------------------------------------------------------------------------------ */

    /**
     * 채팅방 입장
     * @param roomId 입장할 채팅방의 ID
     * @return 해당 채팅방의 정보
     */
    @GetMapping("/room/enter/{roomId}")
    @ResponseBody
    public List<ChatDto> enterChatRoom(@PathVariable String roomId) throws IOException {
        log.info(">>>>>>>>>>>>>>>>> enterChatRoom() invoked");
//        log.info("chatService.findChatRoomLogs(roomId): {}", chatService.findChatRoomLogs(roomId));

        return chatService.findChatRoomLogs(roomId);

    } // enterChatRoom()

} // end class