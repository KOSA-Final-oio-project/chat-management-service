package com.oio.chatservice.controller;

import com.oio.chatservice.dto.ChatRoomDto;
import com.oio.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping // 새로운 채팅 방을 생성하는 엔드포인트(/chat)에 사용
    public ChatRoomDto createRoom(@RequestParam String name) {
        // URL의 쿼리 파라미터로 전달된 name 값을 사용 -> 새로운 채팅 방을 생성 -> 생성된 MessageRoom 객체를 반환
        return chatService.createRoom(name);
    }

    @GetMapping
    public List<ChatRoomDto> findAllRoom() {
        // 시스템에 존재하는 모든 채팅 방의 목록(List<MessageRoom>)을 반환
        return chatService.findAllRoom();
    }

} // end class
