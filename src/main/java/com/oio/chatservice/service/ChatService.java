package com.oio.chatservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oio.chatservice.dto.ChatDto;
import com.oio.chatservice.dto.ChatRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<String, ChatRoomDto> chatRoomsMap;

    // 파일 위치 및 날짜 포맷 지정
    private static final String BASE_PATH = "C:\\Users\\JeonSein\\Desktop\\Chat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @PostConstruct
    private void init() {
        chatRoomsMap = new LinkedHashMap<>();
    } // init()

    /**
     * 모든 채팅방을 조회
     * @return 모든 채팅방 목록 (최신 생성된 채팅방부터 반환)
     */
    public List<ChatRoomDto> findAllChatRoom() {
        // 채팅방 생성 순서 = 최근순 반환
        List chatRoomsList = new ArrayList<>(chatRoomsMap.values());
        Collections.reverse(chatRoomsList);

        return chatRoomsList;
    } // findAllChatRoom()

    /**
     * 주어진 채팅방 ID에 해당하는 채팅방을 찾음
     * @param roomId 찾고자 하는 채팅방의 ID
     * @return 찾아진 채팅방, 없으면 null 반환
     */
    public ChatRoomDto findChatRoomById(String roomId) {
        return chatRoomsMap.get(roomId);
    } // findChatRoomById()

    /**
     * 새로운 채팅방을 생성,
     * @param name 생성할 채팅방의 이름
     * @return 생성된 채팅방 정보를 반환
     */
    public ChatRoomDto createChatRoom(String name) {
        ChatRoomDto chatRoomDto = ChatRoomDto.createChatRoom(name);
        chatRoomsMap.put(chatRoomDto.getRoomId(), chatRoomDto);

        return chatRoomDto;
    } // createChatRoom()

    /* ------------------------------------------------------------------------------------ */

    /**
     * 채팅 메시지 정보를 파일에 저장
     * 파일명은 채팅방 ID와 현재 날짜를 포함
     * @param chatDto 저장할 채팅 메시지 정보
     */
    private void saveChatToText(ChatDto chatDto) {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = String.format("%s_%s.txt", chatDto.getRoomId(), date); // roomId를 파일명에 사용

        File directory = new File(BASE_PATH);

        if (!directory.exists()) {
            directory.mkdirs();
        } // if

        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String jsonMessage = objectMapper.writeValueAsString(chatDto);
            writer.write(jsonMessage + "\n");
        } catch (IOException e) {
            log.error("Error writing to file", e);
        } // try-catch

    } // saveChatToText

} // end class
