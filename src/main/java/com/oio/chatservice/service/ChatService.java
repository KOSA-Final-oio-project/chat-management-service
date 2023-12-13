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

    /* ------------------------------------------------------------------------------------ */

    // 파일 위치 및 날짜 포맷 지정
    private static final String BASE_PATH = "C:\\Users\\JeonSein\\Desktop\\Chat";
    private static final String UNMERGED_CHAT_PATH = BASE_PATH + "/unmergedChat";
    private static final String MERGED_CHAT_PATH = BASE_PATH + "/mergedChat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /* ------------------------------------------------------------------------------------ */

    @PostConstruct
    private void init() {
        chatRoomsMap = new LinkedHashMap<>();
    } // init()

    /* ------------------------------------------------------------------------------------ */

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
     * 주어진 이메일(email)을 포함하는 채팅방 목록을 조회하는 메소드
     * @param email 조회하려는 사용자의 이메일
     * @return 해당 이메일이 포함된 채팅방 목록
     * @throws IOException 파일 읽기 중 발생할 수 있는 예외
     */
    public List<ChatRoomDto> findChatRoomByEmail(String email) throws IOException {

        List<ChatRoomDto> filterdRoomList = new ArrayList<>();

        // MERGED_CHAT_PATH에 위치한 모든 파일을 탐색
        try (Stream<Path> paths = Files.walk(Paths.get(MERGED_CHAT_PATH))) {
            // 채팅방 ID를 추출하는 로직
            List<String> roomIds = paths
                    .filter(Files::isRegularFile) // 정규 파일만 필터링
                    .map(path -> path.getFileName().toString()) // 파일명을 문자열로 변환
                    .filter(filename -> filename.contains(email)) // 이메일을 포함하는 파일명만 필터링
                    .map(filename -> filename.split("_")[0]) // 파일명에서 roomId 추출
                    .distinct() // 중복 제거
                    .collect(Collectors.toList()); // 결과를 리스트로 수집

            // 추출된 각 채팅방 ID에 해당하는 채팅방 정보 조회
            for (String roomId : roomIds) {
                ChatRoomDto room = chatRoomsMap.get(roomId);
                if (room != null) {
                    filterdRoomList.add(room); // 채팅방 정보가 있으면 리스트에 추가
                } // if
            } //  for
        } // try

        log.info("filterdRoomList: {}", filterdRoomList);
        return filterdRoomList; // 필터링된 채팅방 목록 반환

    } // findChatRoomByEmail()

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

    /**
     * roomId로 채팅 내역 찾기
     * @param roomId 채팅방 아이디
     * @return 채팅방 아이디에 맞는 채팅 내역
     * @throws IOException
     */
    public List<ChatDto> findChatRoomLogs(String roomId) throws IOException {
        List<ChatDto> chatLogs = new ArrayList<>();

        File folder = new File(MERGED_CHAT_PATH);

        File[] listOfFiles = folder.listFiles();

        // 해당 roomId를 포함하는 파일 찾기
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith(roomId)) {
                try (Stream<String> lines = Files.lines(file.toPath())) {
                    lines.forEach(line -> {
                        try {
                            ChatDto chatLog = objectMapper.readValue(line, ChatDto.class);
                            chatLogs.add(chatLog);
                        } catch (IOException e) {
                            log.error("Error parsing chat log: ", e);
                        } // try-catch
                    }); // forEach
                } // try
                break; // roomId에 해당하는 파일을 찾으면 반복 중지
            } // if
        } // for

        return chatLogs;
    } // findChatRoomLogs()

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
     * @throws IOException 파일 읽기 중 발생할 수 있는 예외
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
            log.error("Error occurred in saveChatToText() while writing chat file: ", e);
        } // try-catch

        // 각 채팅 파일 하나로 합치기
        mergeChatFilesByRoomId(chatDto.getRoomId());

    } // saveChatToText()

    /**
     * 채팅방 ID에 해당하는 모든 채팅 메시지 파일을 하나의 파일 합치는 메소드
     * 합쳐진 파일명 => UUID_sender1_sender2 (이때, sender1은 더 먼저 메시지 보낸 사람)
     * @param roomId 채팅방 ID(=uuid)
     * @throws IOException 파일 읽기 중 발생할 수 있는 예외
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

        /* ========== #1. 파일 탐색 및 필터링 시작! ========== */
        // 1-1. UNMERGED_CHAT_PATH 경로에서 시작하여 모든 파일과 디렉토리를 순회해줌
        try (Stream<Path> files = Files.walk(Paths.get(UNMERGED_CHAT_PATH))) {
            files
                    .filter(Files::isRegularFile) // 1-2. 디렉토리 순회 중 디렉토리를 제외한 파일만 필터링
                    .filter(path -> path.getFileName().toString().startsWith(roomId)) // 1-3. 파일 이름이 주어진 roomId로 시작하는 파일만 필터링
                    /* ========== #2. 각 파일의 채팅 메시지 처리 ========== */
                    .forEach(path -> {
                        try (Stream<String> lines = Files.lines(path)) { // 2-1. 파일의 각 줄을 읽어 스트림으로 반환함, 이때 각 줄은 채팅 메시지의 JSON!!
                            lines.map(line -> { // 2-2. 각 라인을 JSON -> ChatDto 변환
                                        try {
                                            return objectMapper.readValue(line, ChatDto.class);
                                        } catch (IOException e) {
                                            log.error("Error occurred in mergeChatFilesByRoomId() while parsing chat message: ", e);
                                            return null;
                                        } // inner-try-catch
                                    })
                                    .filter(Objects::nonNull) // 2-3. null 값이 아닌 객체만 필터링
                                    .forEach(chatDto -> { // 2-4. 각 ChatDto 객체 순회하면서 작업 수행함
                                        // 2-5. senderSet에 고유 sender를 추가 (최대 2명의 sender만 저장됨. 1:1이라..이렇게 했는데 의미 없는거 같기도 하고...)
                                        if (senderSet.size() < 2 && !senderSet.contains(chatDto.getSender())) {
                                            senderSet.add(chatDto.getSender());
                                        } // if
                                        allMessageList.add(chatDto); // 2-6. 모든 메시지를 allMessageList에 추가
                                    }); // forEach
                        } catch (IOException e) {
                            log.error("Error occurred in mergeChatFilesByRoomId() while reading chat file: ", e);
                        } // try-catch
                    }); // forEach
        } // try

        /* ========== #3. sendDate로 오래된순 -> 최신순으로 정렬해줌 ========== */
        allMessageList.sort(Comparator.comparing(ChatDto::getSendDate));

        /* ========== #4. 발신자 추출 (최초 sender 2명 -> 리스트에 담아줌) ========== */
        List<String> senderList = allMessageList.stream()
                .map(ChatDto::getSender) // 4-1. ChatDto 객체에서 각 sender 값을 추출
                .distinct() // 4-2. 스트림에서 중복된 요소를 제거 (동일 sender 여러개여도 하나만 유지)
                .limit(2) // 4-3. 스트림의 요소 수를 제한(= 처음 sender 2명만 유지됨)
                .collect(Collectors.toList()); // 4-4. 스트림의 요소를 원하는 컬렉션(= 리스트)으로 변환 -> sender 2명이 리스트로 변환됨

        /* ========== #5. sender 2명이어야지 .txt 파일 생성될 수 있도록 조건 추가 ========== */
        if (senderList.size() == 2) {

            // 5-1.
            // 더 빨리 메시지 보낸 사람 = sender1 => 대화 거는 사람이 sender1
            // 상품 주인이 sender2가 됨
            // 나중에 messageType ENTER 걸러주기
            String sender1 = senderList.get(0);
            String sender2 = senderList.get(1);

            // 5-2. 파일 합본의 이름 = roomId_대여하려고하는사람_대여해주는사람.txt
            String mergedFilePath = MERGED_CHAT_PATH + "/" + roomId + "_" + sender1 + "_" + sender2 + ".txt";

            // 5-3. 파일 작성
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFilePath))) {
                for (ChatDto message : allMessageList) {
                    // 정렬된 모든 메시지를 JSON 형식으로 다시 변환해서 텍스트 파일로 저장
                    writer.write(objectMapper.writeValueAsString(message) + "\n");
                } // for
            } // try
        } // if

    } // mergeChatFilesByRoomId()

} // end class