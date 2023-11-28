package com.oio.chatservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oio.chatservice.dto.ChatDto;
import com.oio.chatservice.dto.ChatRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<String, ChatRoomDto> chatRooms;

    // 파일 위치 및 날짜 포맷 지정
    private static final String BASE_PATH = "C:\\Users\\JeonSein\\Desktop\\Chat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");


    // messageRooms Map을 LinkedHashMap으로 초기화하여 메시지 방들을 관리 (삽입 순서 유지를 위해 Linked 어쩌구 사용)
    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    /**
     * 모든 채팅방을 리스트 형태로 반환
     * @return messageRooms.values()를 사용해서 현재 Map에 저장된 모든 MessageRoom 객체 가져옴
     * -> 새로운 ArrayList에 담아 반환
     */
    public List<ChatRoomDto> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }

    /**
     * 특정 ID를 가진 방을 roomId로 찾음
     * @param roomId 채팅방ID
     * @return messageRooms.get(roomId)를 통해 해당 ID를 가진 방을 Map에서 찾아 반환
     */
    public ChatRoomDto findById(String roomId) {
        return chatRooms.get(roomId);
    }

    /**
     * 채팅방 생성
     * @param name 생성할 방의 이름
     * @return
     */
    public ChatRoomDto createRoom(String name) {
        String roomId = name;

        ChatRoomDto newRoom = ChatRoomDto.builder().roomId(roomId).build();
        chatRooms.put(roomId, newRoom);

        return newRoom;
    }

    /* ------------------------------------------------------------------------------------ */

    // 객체 생성 시점에 @PostCounstruct 를 이용해 초기화를 진행
    // 이 초기화 콜백 시점에 해당 객체의 초기화 작업
    // 스프링 빈 생성 → 생성자로 의존관계 주입 → @PostConstruct 메서드 호출
    /*
    @PostConstruct
    private void initChatLogFile() {
        File directory = new File(BASE_PATH, CHAT_LOGS_FOLDER_NAME);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File chatLogFile = new File(directory, CHAT_LOG_FILE_NAME);

        if (!chatLogFile.exists()) {
            try {
                chatLogFile.createNewFile();
            } catch (IOException e) {
                log.error("Error creating chat log file", e);
            }
        }
    }
    */

    // 채팅 기록을 파일에 저장
    // 채팅 기록을 파일에 저장하는 메서드 수정
    private void saveChatToText(ChatDto chatDto) {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = String.format("%s_%s.txt", chatDto.getRoomId(), date); // roomId를 파일명에 사용

        File directory = new File(BASE_PATH);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String jsonMessage = objectMapper.writeValueAsString(chatDto);
            writer.write(jsonMessage + "\n");
        } catch (IOException e) {
            log.error("Error writing to file", e);
        }

    } // saveChatToText

    /* ------------------------------------------------------------------------------------ */

    /**
     * 메시지 전송
     * @param session 메시지를 보낼 WebSocket 세션
     * @param message
     * @param <T>
     */
    // 메시지 전송 메서드
    public <T> void sendMessage(WebSocketSession session, T message) {
        log.info("sendMessage() invoked.");

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));

            if (message instanceof ChatDto) {
                saveChatToText((ChatDto) message);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    } // sendMessage()

} // end class
