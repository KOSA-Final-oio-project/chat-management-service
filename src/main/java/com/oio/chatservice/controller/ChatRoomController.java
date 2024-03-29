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
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class ChatRoomController {

    private final ChatService chatService;

    /* ------------------------------------------------------------------------------------ */

    /**
     * 채팅방 생성
     * @param chatRoomDto
     * @return 채팅방 정보
     */
    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto createChatRoom (ChatRoomDto chatRoomDto) {

        chatRoomDto.setRoomId(UUID.randomUUID().toString());
        chatRoomDto.setSender(chatRoomDto.getSender());
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}",chatRoomDto.getSender());
        log.info("생성 요청된 채팅방 정보: {}", chatRoomDto);
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
     * nickname에 해당하는 채팅방 목록 반환
     * @return 채팅방 목록 데이터
     */
    @GetMapping("/rooms/{nickname}")
    @ResponseBody
    public List<ChatRoomDto> chatRoomByNickname(@PathVariable String nickname) throws IOException {
        log.info(">>>>>>>>>>>>>>>>> chatRoomByNickname() invoked");
        log.info(">>>>>>>>>>>>>>>>> 전달받은 nickname: " + nickname);

        chatService.loadChatRooms(nickname); // 사용자별 채팅방 목록 로드
        return chatService.findChatRoomByNickname(nickname);
    } // chatRoomByNickname()

    /**
     * roomId로 채팅 내역 조회 후 첫 줄 반환
     * @param roomId
     * @return
     * @throws IOException
     */
    @GetMapping("/roominfo/{roomId}")
    @ResponseBody
    public ChatRoomDto chatRoomInfoByRoomId(@PathVariable String roomId) throws IOException {
        log.info(">>>>>>>>>>>>>>>>> chatRoomInfoByRoomId() invoked");
        log.info(">>>>>>>>>>>>>>>>> 전달받은 roomId: " + roomId);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", chatService.findInfoByRoomId(roomId));

        return chatService.findInfoByRoomId(roomId);
    } // chatRoomInfoByRoomId()

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

        return chatService.findChatRoomLogs(roomId);
    } // enterChatRoom()

} // end class