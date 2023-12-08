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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    public void saveChatToText(ChatDto chatDto) {
        String date = LocalDateTime.now().format(DATE_FORMATTER); // 채팅 내역 저장되는 날짜

        String fileName = String.format("%s_%s_%s.txt", chatDto.getRoomId(), chatDto.getSender(), date); // roomId를 파일명에 사용
        String senderFolder = BASE_PATH + "/" + chatDto.getSender(); // 발신자 이름으로 폴더 경로 생성
        String filePath = senderFolder + "/" + fileName; // 폴더 경로와 파일 이름을 조합하여 최종 파일 경로로 해줌

        File directory = new File(senderFolder);
        File file = new File(filePath);

        if (!directory.exists()) {
            directory.mkdirs(); // 발신자 폴더가 없으면 생성
        } // if

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String jsonMessage = objectMapper.writeValueAsString(chatDto);
            writer.write(jsonMessage + "\n");
        } catch (IOException e) {
            log.error("Error invoked in saveChatToText(): ", e);
        } // try-catch

    } // saveChatToText
    /*

    // 폴더 경로와 파일 이름을 조합하여 최종 파일 경로 생성
    String filePath = senderFolder + "/" + chatDto.getRoomId() + "_" + date + ".txt";

    File directory = new File(senderFolder);
    File file = new File(filePath);

    if (!directory.exists()) {
        directory.mkdirs(); // 발신자 폴더가 없으면 생성
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
        String jsonMessage = objectMapper.writeValueAsString(chatDto);
        writer.write(jsonMessage + "\n");
    } catch (IOException e) {
        log.error("Error invoked in saveChatToText(): ", e);
    }
}
     */

    /**
     * 채팅방 ID에 따른 채팅 내역을 읽어오는 메소드
     * @param roomId
     * @return
     */
    public List<ChatDto> readChatHistory(String roomId) { // 채팅 내역 -> roomId로 읽어옴
        // 채팅 내역을 저장할 ChatDto 객체 리스트 만들어주기
        List<ChatDto> chatHistory = new ArrayList<>();

        // Files.walk(Paths.get(BASE_PATH))
        // ->  BASE_PATH 경로 및 하위 경로에 있는 모든 파일&디렉토리의 경로를 스트림으로 생성해줌!
        try (Stream<Path> paths = Files.walk(Paths.get(BASE_PATH))) {

            // 필터링 및 파일 경로 수집
            List<String> fileNames = paths
                    .filter(Files::isRegularFile) // 디렉토리가 아닌 파일만 필터링
                    .map(Path::toString) // 경로(Path)를 문자열로 변환
                    .filter(path -> path.contains(roomId)) // 파일 경로에 roomId가 포함된 것만 필터링
                    .collect(Collectors.toList()); // 필터링된 경로를 리스트로 수집

            // 채팅 내역 읽기 및 파싱
            for (String fileName : fileNames) { // 수집된 파일명 리스트 돌리기!

                // 각 파일 읽어서 내용을 문자열 리스트로 변환해줌
                List<String> lines = Files.readAllLines(Paths.get(fileName));

                // 파일의 라인 돌리기
                for (String line : lines) {
                    // JSON -> Dto
                    ChatDto chatDto = objectMapper.readValue(line, ChatDto.class);

                    // roomId 동일한 것만 추가
                    if (chatDto.getRoomId().equals(roomId)) {
                        chatHistory.add(chatDto);
                    } // if

                } // inner-for

            } // outer-for
        } catch (IOException e) {
            e.printStackTrace();
        } // try-catch

        // ChatDto 리스트를 반환
        return chatHistory;
    }

} // end class
