package com.oio.chatservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oio.chatservice.dto.ChatDto;
import com.oio.chatservice.dto.ChatRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<String, ChatRoomDto> chatRoomsMap;

    /* ------------------------------------------------------------------------------------ */

    // 파일 위치 및 날짜 포맷 지정
    private static final String BASE_PATH = "C:\\Users\\JeonSein\\Desktop\\Chat";

    /* ------------------------------------------------------------------------------------ */

    @PostConstruct
    private void init() {
        chatRoomsMap = new LinkedHashMap<>();
    } // init()

    /* ------------------------------------------------------------------------------------ */

    // 채팅방 목록 로드시켜주기
    public void loadChatRooms(String email) {

        File chatDirectory = new File(BASE_PATH);

        if (chatDirectory.exists() && chatDirectory.isDirectory()) {
            File[] files = chatDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        // 파일 이름에서 ".txt" 확장자 제거
                        String fileNameWithoutExtension = file.getName().substring(0, file.getName().length() - 4);
                        // 파일 이름을 "_"로 분할
                        String[] parts = fileNameWithoutExtension.split("_");
                        // 파일 이름의 각 부분을 검사하여 이메일과 일치하는지 확인
                        for (String part : parts) {
                            if (part.equals(email)) { // 이메일이 일치하는 경우
                                String roomId = parts[0]; // roomId 추출
                                ChatRoomDto chatRoom = createChatRoomDto(roomId, file);
                                chatRoomsMap.put(chatRoom.getRoomId(), chatRoom); // chatRoomsMap에 추가
                                log.info("chatRoomsMap: {}", chatRoomsMap);
                                break; // 일치하는 이메일을 찾았으므로 더 이상 검사하지 않음
                            }
                        }
                    }
                }
            }
        }
    }

    /* ------------------------------------------------------------------------------------ */

    /**
     * map 초기화하고 데이터 삽입
     * @param roomId
     * @param file
     * @return
     */
    private ChatRoomDto createChatRoomDto(String roomId, File file) {
        ChatRoomDto chatRoom = new ChatRoomDto();
        chatRoom.setRoomId(roomId);

        // 파일 이름을 "_" 기준으로 분할
        String[] parts = file.getName().split("_");

        if (parts.length >= 5) {  // 파일명이 예상한 형식을 따르는지 확인
            // roomId는 이미 인자로 전달받음
            String roomName = parts[1]; // roomName 부분 추출
            String createDate = parts[2]; // createDate 부분 추출
            String sender = parts[3]; // sender 부분 추출
            String receiver = parts[4].replace(".txt", ""); // receiver 부분 추출 (".txt" 제거)

            // ChatRoomDto에 정보 설정
            chatRoom.setRoomName(roomName);
            chatRoom.setCreateDate(createDate);
            chatRoom.setSender(sender);
            chatRoom.setReceiver(receiver);
            // 추가로 필요한 정보가 있다면 여기에 설정
        }

        log.info(" chatRoom: {} ", chatRoom);
        return chatRoom;
    }

    /**
     * chatDto의 date -> 'yyyy-MM-dd HH:mm:ss' 형식으로 변환
     * @param dateString
     * @return
     */
    private String convertDate(String dateString) {
        // dateString을 원하는 날짜 형식으로 변환하는 로직
        // "20231215032142" -> "2023-12-15 03:21:42"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

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
     * 이메일로 채팅방 찾기
     * @param email
     * @return
     * @throws IOException
     */
    public List<ChatRoomDto> findChatRoomByEmail(String email) throws IOException {
        List<ChatRoomDto> filterdRoomList = new ArrayList<>();

        // BASE_PATH에 위치한 모든 파일을 탐색
        try (Stream<Path> paths = Files.walk(Paths.get(BASE_PATH))) {

            // 채팅방 ID를 추출하는 로직
            List<String> roomIds = paths
                    .filter(Files::isRegularFile) // 정규 파일만 필터링
                    .map(path -> path.getFileName().toString()) // 파일명을 문자열로 변환
                    .filter(filename -> {
                        String[] parts = filename.split("_");
                        // 파일명에서 sender 또는 receiver가 주어진 이메일과 일치하는지 확인
                        return parts.length >= 5 && (parts[3].equals(email) || parts[4].startsWith(email));
                    })
                    .map(filename -> filename.split("_")[0]) // 파일명에서 roomId 추출
                    .distinct() // 중복 제거
                    .collect(Collectors.toList()); // 결과를 리스트로 수집

            log.info("roomIds: {}" , roomIds);

            // 추출된 각 채팅방 ID에 해당하는 채팅방 정보 조회
            for (String roomId : roomIds) {
                ChatRoomDto room = chatRoomsMap.get(roomId);
                log.info("ChatRoomDto room = chatRoomsMap.get(roomId) {}", room);
                if (room != null) {
                    filterdRoomList.add(room); // 채팅방 정보가 있으면 리스트에 추가
                } // if
            } // for

//            log.info("chatRoomsMap: {}",chatRoomsMap);
            log.info("chatRoomsMap.size(): {}",chatRoomsMap.size());

        } // try

//        log.info("filterdRoomList: {}", filterdRoomList);
        log.info("filterdRoomList.size(): {}", filterdRoomList.size());
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


    // 채팅 내역 찾기
    public List<ChatDto> findChatRoomLogs(String roomId) throws IOException {
        List<ChatDto> chatLogs = new ArrayList<>();
        File folder = new File(BASE_PATH);
        File[] listOfFiles = folder.listFiles();

        // 해당 roomId를 포함하는 파일 찾기
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith(roomId)) {
                AtomicInteger lineNumber = new AtomicInteger(); // Line number counter
                try (Stream<String> lines = Files.lines(file.toPath())) {
                    lines.forEach(line -> {
                        lineNumber.getAndIncrement(); // Increment line number for each line
                        if (lineNumber.get() > 1) { // Skip the first line
                            try {
                                ChatDto chatLog = objectMapper.readValue(line, ChatDto.class);
                                chatLogs.add(chatLog);
                            } catch (IOException e) {
                                log.error("Error occurred in findChatRoomLogs() while parsing chat log: ", e);
                            }
                        }
                    });
                } // try
                break; // roomId에 해당하는 파일을 찾으면 반복 중지
            } // if
        } // for

        return chatLogs;
    } // findChatRoomLogs()


    /* ------------------------------------------------------------------------------------ */

    /**
     * roomId로 채팅 내역 찾기
     * @param roomId 채팅방 아이디
     * @return 채팅방 아이디에 맞는 채팅 내역
     * @throws IOException
     */
    private String findChatFileName(String roomId, String sender) {
        File chatDir = new File(BASE_PATH);

        if (!chatDir.exists() || !chatDir.isDirectory()) {
            // Chat 폴더가 존재하지 않거나 디렉토리가 아닌 경우
            return null;
        }

        // Chat 폴더 내의 모든 파일을 순회
        File[] files = chatDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                // 파일명이 roomId와 sender 또는 receiver를 포함하는지 확인
                if (filename.startsWith(roomId + "_") && (filename.contains("_" + sender + "_") || filename.endsWith("_" + sender + ".txt"))) {
                    log.info(filename);
                    return filename; // 조건에 맞는 파일명 반환
                }
            }
        }
        return null; // 해당하는 파일을 찾지 못한 경우
    }

    /* ------------------------------------------------------------------------------------ */

    /**
     * 채팅 메시지 정보를 파일에 저장
     * 파일명은 채팅방 ID와 현재 날짜를 포함
     * @param chatDto 저장할 채팅 메시지 정보
     * @throws IOException 파일 읽기 중 발생할 수 있는 예외
     */
    public void saveChatToText(ChatDto chatDto) throws IOException {
        // findChatFileName 메소드를 사용하여 파일 이름을 조회
        String filename = findChatFileName(chatDto.getRoomId(), chatDto.getSender());
        // 현재 시간을 "yyyy-MM-dd HH:mm:ss" 포맷으로 설정
        String formattedSendDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        chatDto.setSendDate(formattedSendDate);
        log.info(">>>>>>>>>>>>> "+chatDto.getSendDate());

        if (filename == null) {
            return;
        }

        // 파일 경로 설정
        String filePath = BASE_PATH + "/" + filename;
        File file = new File(filePath);

        // 파일이 존재하지 않으면 생성
        if (!file.exists()) {
            file.createNewFile();
        }

        // 메시지를 파일에 추가
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(objectMapper.writeValueAsString(chatDto) + "\n");
        } catch (IOException e) {
            log.error("Error occurred in saveChatToText() while writing chat file: ", e);
        }
    }

    /* ------------------------------------------------------------------------------------ */

    // 방 생성하자마자 파일로 저장
    public ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto) {
        // 채팅방 생성
        ChatRoomDto newChatRoom = ChatRoomDto.createChatRoom(
                chatRoomDto.getRoomName(),
                chatRoomDto.getProductName(),
                chatRoomDto.getProductPrice(),
                chatRoomDto.getReceiver(),
                chatRoomDto.getSender()
        );

        // 파일 이름 생성 (roomId_roomName_createDate_sender_receiver.txt)
        String fileName = newChatRoom.getRoomId() + "_" +
                newChatRoom.getRoomName() + "_" +
                newChatRoom.getCreateDate().replace(":", "").replace(" ", "") + "_" +
                newChatRoom.getSender() + "_" +
                newChatRoom.getReceiver() + ".txt";

        // 파일 생성 및 ChatRoomDto 내용 쓰기
        writeNewChatRoomToFile(fileName, newChatRoom);

        // Map에 채팅방 추가
        chatRoomsMap.put(newChatRoom.getRoomId(), newChatRoom);

        log.info(">>>>>>>>>>>>>>>>>>>>>>> New Chat Room Created: {}", newChatRoom);

        return newChatRoom;
    }

    // 생성된 빈 파일에 newChatRoomDto 적어주기
    private void writeNewChatRoomToFile(String fileName, ChatRoomDto chatRoomDto) {
        File file = new File(BASE_PATH + File.separator + fileName);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            // 채팅방 정보를 파일에 쓰기
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(objectMapper.writeValueAsString(chatRoomDto));
                writer.newLine(); // 줄바꿈
            }
        } catch (IOException e) {
            log.error("Error writing new chat room to file: {}", e.getMessage());
        }
    }

} // end class