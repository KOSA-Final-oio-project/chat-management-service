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
@RequestMapping("/chat")
//@CrossOrigin(origins = "http://localhost:5173")
@CrossOrigin(origins = "*", allowedHeaders = "*") // 모든 오리진과 헤더를 허용
public class ChatRoomController {

    private final ChatService chatService;

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

    /**
     * email에 해당하는 채팅방 목록 반환
     * @return 채팅방 목록 데이터
     */
    @GetMapping("/rooms/{email}")
    @ResponseBody
    public List<ChatRoomDto> chatRoomByEmail(@PathVariable String email) throws IOException {
        log.info(">>>>>>>>>>>>>>>>> chatRoomByEmail() invoked");
        log.info("email" + email);

//        chatService.loadChatRoomsForUser(email); // 사용자별 채팅방 목록 로드
        chatService.loadChatRoomsForUser(); // 사용자별 채팅방 목록 로드
        return chatService.findChatRoomByEmail(email);
    } // chatRoom()

//    /**
//     * 채팅방 생성
//     * @param name 생성할 채팅방의 이름
//     * @return 생성된 채팅방 정보
//     */
//    @PostMapping("/room/{name}")
//    @ResponseBody
//    public ChatRoomDto createChatRoom(@PathVariable String name) {
//        log.info(">>>>>>>>>>>>>>>>> createChatRoom() invoked");
//        log.info("생성된 채팅방의 이름은: "  + name);
//
//        return chatService.createChatRoom(name);
//    } // createChatRoom()
    @PostMapping("/room/{roomName}/{createDate}/{productName}/{productPrice}/{receiver}/{email}")
    @ResponseBody
    public ChatRoomDto createChatRoom(
            @PathVariable String roomName,
            @PathVariable String createDate,
            @PathVariable String productName,
            @PathVariable String productPrice,
            @PathVariable String receiver,
            @PathVariable String email) {

        // ChatRoomDto 객체 생성
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setRoomId(UUID.randomUUID().toString());
        chatRoomDto.setRoomName(roomName);
        chatRoomDto.setCreateDate(createDate); // 또는 서버 시간 사용
        chatRoomDto.setProductName(productName);
        chatRoomDto.setProductPrice(productPrice);
        chatRoomDto.setReceiver(receiver);
        chatRoomDto.setEmail(email);

        log.info("생성 요청된 채팅방 정보: {}", chatRoomDto);
        return chatService.createChatRoom(chatRoomDto);

//        return chatRoomDto;
    }

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

    /**
     * 채팅방 입장
     * @param roomId 입장할 채팅방의 ID
     * @return 해당 채팅방의 정보
     */
    @GetMapping("/room/enter/{roomId}")
    @ResponseBody
    public List<ChatDto> enterChatRoom(@PathVariable String roomId) throws IOException {
        log.info(">>>>>>>>>>>>>>>>> enterChatRoom() invoked");
        log.info("chatService.findChatRoomLogs(roomId): {}", chatService.findChatRoomLogs(roomId));

        return chatService.findChatRoomLogs(roomId);

    } // enterChatRoom()

} // end class