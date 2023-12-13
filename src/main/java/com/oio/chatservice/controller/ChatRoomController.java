package com.oio.chatservice.controller;

import com.oio.chatservice.dto.ChatDto;
import com.oio.chatservice.dto.ChatRoomDto;
import com.oio.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:5173")
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

        return chatService.findChatRoomByEmail(email);
    } // chatRoom()

    /**
     * 채팅방 생성
     * @param name 생성할 채팅방의 이름
     * @return 생성된 채팅방 정보
     */
    @PostMapping("/room/{name}")
    @ResponseBody
    public ChatRoomDto createChatRoom(@PathVariable String name) {
        log.info(">>>>>>>>>>>>>>>>> createChatRoom() invoked");
        log.info("생성된 채팅방의 이름은: "  + name);

        return chatService.createChatRoom(name);
    } // createChatRoom()

    /**
     * 특정 채팅방 조회
     * @param roomId 조회할 채팅방의 ID
     * @return 조회된 채팅방 정보
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto chatRoomInfo(@PathVariable String roomId) {
        log.info(">>>>>>>>>>>>>>>>> chatRoomInfo() invoked");

        ChatRoomDto chatRoomDto = chatService.findChatRoomById(roomId);

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