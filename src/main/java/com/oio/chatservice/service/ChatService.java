package com.oio.chatservice.service;

import com.fasterxml.jackson.databind.JsonNode;
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

    /* ------------------------------------------------------------------------------------ */

    /**
     * 모든 채팅방을 조회 (이메일)
     * @return 모든 채팅방 목록 (최신 생성된 채팅방부터 반환)
     */
    public List<ChatRoomDto> findAllChatRoom() {
        // 채팅방 생성 순서 = 최근순 반환
        List chatRoomsList = new ArrayList<>(chatRoomsMap.values());
        Collections.reverse(chatRoomsList);

        return chatRoomsList;
    } // findAllChatRoom()

    // 로그인 이후 헤더에서 email값 가져와서 해당 email에 맞는 방만 조회할 수 있게
//    public List<ChatRoomDto> findAllChatRoom(String email) {
//        // 채팅방 생성 순서 = 최근순 반환
//        List chatRoomsList = new ArrayList<>(chatRoomsMap.values());
//        Collections.reverse(chatRoomsList);
//
//        return chatRoomsList;
//    } // findAllChatRoom()

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
    public void saveChatToText(ChatDto chatDto) throws IOException {
        String date = LocalDateTime.now().format(DATE_FORMATTER); // 채팅 내역 저장되는 날짜

        String fileName = String.format("%s_%s_%s.txt", chatDto.getRoomId(), chatDto.getSender(), date); // roomId를 파일명에 사용
        String senderFolder = BASE_PATH + "/" + "unmergedChat" + "/" + chatDto.getSender(); // 발신자 이름으로 폴더 경로 생성
        String filePath = senderFolder + "/" + fileName; // 폴더 경로와 파일 이름을 조합하여 최종 파일 경로로 해줌

        File directory = new File(senderFolder);
        File file = new File(filePath);

        if (!directory.exists()) {
            directory.mkdirs(); // 발신자 폴더가 없으면 생성
        } // if

        // 새 메시지를 파일에 저장

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String jsonMessage = objectMapper.writeValueAsString(chatDto);
            writer.write(jsonMessage + "\n");
        } catch (IOException e) {
            log.error("Error occurred in saveChatToText(): ", e);
        } // try-catch

        mergeChatFilesByRoomId(chatDto.getRoomId());

    }

    public void mergeChatFilesByRoomId(String roomId) throws IOException {
        String unmergedFolderPath = BASE_PATH + "/unmergedChat";
        String mergedFolderPath = BASE_PATH + "/mergedChat";

        File mergedFolder = new File(mergedFolderPath);
        if (!mergedFolder.exists()) {
            mergedFolder.mkdirs();
        }

        String mergedFilePath = mergedFolderPath + "/" + roomId + "_merged.txt";
        File mergedFile = new File(mergedFilePath);

        Set<String> senders = new LinkedHashSet<>();
        List<ChatDto> allMessages = new ArrayList<>();

        try (Stream<Path> files = Files.walk(Paths.get(unmergedFolderPath))) {
            files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(roomId))
                    .forEach(path -> {
                        try (Stream<String> lines = Files.lines(path)) {
                            lines.map(line -> {
                                        try {
                                            return objectMapper.readValue(line, ChatDto.class);
                                        } catch (IOException e) {
                                            log.error("Error parsing chat message: ", e);
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .forEach(chatDto -> {
                                        if (senders.size() < 2 && !senders.contains(chatDto.getSender())) {
                                            senders.add(chatDto.getSender());
                                        }
                                        allMessages.add(chatDto);
                                    });
                        } catch (IOException e) {
                            log.error("Error reading chat file: ", e);
                        }
                    });
        }

        allMessages.sort(Comparator.comparing(ChatDto::getSendDate));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFile))) {
            for (ChatDto message : allMessages) {
                writer.write(objectMapper.writeValueAsString(message) + "\n");
            }
        }

        // Senders are now the first two unique senders in sendDate order
        System.out.println("Unique Senders: " + senders);
    }

} // end class