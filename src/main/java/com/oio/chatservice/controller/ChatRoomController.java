package com.oio.chatservice.controller;

import com.oio.chatservice.dto.ChatRoomDto;
import com.oio.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    }

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
        log.info("조회된 채팅방의 roomId는: "  + roomId);

        ChatRoomDto chatRoomDto = chatService.findChatRoomById(roomId);
        log.info("조회된 채팅방 정보_chatRoomDto.getRoomId() = {}", chatRoomDto.getRoomId());
        log.info("조회된 채팅방 정보_chatRoomDto.getName() = {}", chatRoomDto.getName());

//        return chatService.findChatRoomById(roomId);
        return chatRoomDto;
    }

} // end class