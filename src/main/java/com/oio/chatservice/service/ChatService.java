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
    private static final String UNMERGED_CHAT_PATH = BASE_PATH + "/unmergedChat";
    private static final String MERGED_CHAT_PATH = BASE_PATH + "/mergedChat";
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
     * 채팅 파일의 이름 생성
     * @param roomId 채팅방 ID
     * @param sender 발신자
     * @return 구성된 파일 이름
     */
    private String buildChatFileName(String roomId, String sender) {
        return String.format("%s_%s.txt", roomId, sender);
    } // buildChatFileName()
//    private String buildChatFileName(String roomId, String sender, String date) {
//        return String.format("%s_%s_%s.txt", roomId, sender, date);
//    } // buildChatFileName() -> 이렇게 하면 1분 단위로 날짜 바꼈을 경우 새로운 파일로 생성됨 ㅠ...


    /**
     * 채팅 메시지 정보를 파일에 저장
     * 파일명은 채팅방 ID와 현재 날짜를 포함
     * @param chatDto 저장할 채팅 메시지 정보
     */
    public void saveChatToText(ChatDto chatDto) throws IOException {

        String date = LocalDateTime.now().format(DATE_FORMATTER); // 채팅 내역 저장되는 날짜
        String fileName = buildChatFileName(chatDto.getRoomId(), chatDto.getSender()); // roomId를 파일명에 사용
        String senderFolder = UNMERGED_CHAT_PATH + "/" + chatDto.getSender(); // 발신자 이름으로 폴더 경로 생성
        String filePath = senderFolder + "/" + fileName; // 폴더 경로와 파일 이름을 조합하여 최종 파일 경로로 해줌

        File directory = new File(senderFolder);

        // 발신자 폴더가 없으면 생성
        if (!directory.exists()) {
            directory.mkdirs();
        } // if

        // 새 메시지를 파일에 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(objectMapper.writeValueAsString(chatDto) + "\n");
        } catch (IOException e) {
            log.error("Error occurred in saveChatToText(): ", e);
        } // try-catch

        // 각 채팅 파일 하나로 합치기
        mergeChatFilesByRoomId(chatDto.getRoomId());

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
//            String jsonMessage = objectMapper.writeValueAsString(chatDto);
//            writer.write(jsonMessage + "\n");
//        } catch (IOException e) {
//            log.error("Error occurred in saveChatToText(): ", e);
//        } // try-catch

    } // saveChatToText()

    /**
     * 채팅방 ID에 해당하는 모든 채팅 메시지 파일을 하나의 파일 합치는 메소드
     * 합쳐진 파일명 => UUID_sender1_sender2 (이때, sender1은 더 먼저 메시지 보낸 사람)
     * @param roomId 채팅방 ID(=uuid)
     */
    public void mergeChatFilesByRoomId(String roomId) throws IOException {

        // C:\Users\JeonSein\Desktop\Chat\mergedChat
        File mergedFolder = new File(MERGED_CHAT_PATH);

        // mergedChat 폴더 없으면 생성
        if (!mergedFolder.exists()) {
            mergedFolder.mkdirs();
        } // if

        Set<String> senderSet = new LinkedHashSet<>(); // 각 파일에서 sender 뽑아서 저장
        List<ChatDto> allMessageList = new ArrayList<>(); // 각 파일에서 message 뽑아서 저장

        try (Stream<Path> files = Files.walk(Paths.get(UNMERGED_CHAT_PATH))) {
            files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(roomId))
                    .forEach(path -> {
                        try (Stream<String> lines = Files.lines(path)) {
                            lines.map(line -> {
                                        try {
                                            return objectMapper.readValue(line, ChatDto.class);
                                        } catch (IOException e) {
                                            log.error("Error occurred in parsing chat message: ", e);
                                            return null;
                                        } // inner-try-catch
                                    })
                                    .filter(Objects::nonNull)
                                    .forEach(chatDto -> {
                                        if (senderSet.size() < 2 && !senderSet.contains(chatDto.getSender())) {
                                            senderSet.add(chatDto.getSender());
                                        } // if
                                        allMessageList.add(chatDto);
                                    });
                        } catch (IOException e) {
                            log.error("Error occurred in reading chat file: ", e);
                        } // try-catch
                    }); // forEach
        } // try

        // sendDate로 정렬해줌
        allMessageList.sort(Comparator.comparing(ChatDto::getSendDate));

        List<String> senderList = allMessageList.stream()
                .map(ChatDto::getSender)
                .distinct()
                .limit(2)
                .collect(Collectors.toList());

        // sender 2명이어야지 .txt 파일 생성될 수 있도록 조건 추가
        if (senderList.size() == 2) {

            String sender1 = senderList.get(0);
            String sender2 = senderList.get(1);

            String mergedFilePath = MERGED_CHAT_PATH + "/" + roomId + "_" + sender1 + "_" + sender2 + ".txt";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFilePath))) {
                for (ChatDto message : allMessageList) {
                    writer.write(objectMapper.writeValueAsString(message) + "\n");
                } // for
            } // try
        } // if

    } // mergeChatFilesByRoomId()

} // end class