package com.oio.chatservice.controller;

import com.oio.chatservice.dto.ChatRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatRoomController {

    private final com.oio.chatservice.service.ChatService chatService;

    /**
     * 채팅 리스트 화면(채팅방 목록 페이지를 반환)
     * @param model
     * @return 채팅방 목록 페이지 경로
     */
//    @GetMapping("/room")
//    public String rooms(Model model) {
//        log.info("rooms invoked()");
//        return "/chat/room";
//    }

    /**
     * 모든 채팅방 목록 반환
     * @return 채팅방 목록 데이터
     */
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> chatRoom() {
        log.info("chatRoom invoked()");
        return chatService.findAllChatRoom();
    }

    /**
     * 채팅방 생성
     * @param name 생성할 채팅방의 이름
     * @return 생성된 채팅방 정보
     */
    @PostMapping("/room/{name}")
//    @GetMapping("/room")
    @ResponseBody
    public ChatRoomDto createChatRoom(@PathVariable String name) {
        log.info("createChatRoom invoked()="  + name);
        return chatService.createChatRoom(name);
    }

    /**
     * 채팅방 입장 화면
     * @param model
     * @param roomId 입장할 채팅방의 ID
     * @return 채팅방 상세 페이지 경로
     */
    @GetMapping("/room/enter/{roomId}")
    public String chatRoomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    /**
     * 특정 채팅방 조회
     * @param roomId 조회할 채팅방의 ID
     * @return 조회된 채팅방 정보
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto chatRoomInfo(@PathVariable String roomId) {
        return chatService.findChatRoomById(roomId);
    }

} // end class