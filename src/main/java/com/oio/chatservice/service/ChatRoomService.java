//package com.oio.chatservice.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ChatRoomService {
//
//    @Autowired
//    private SimpMessagingTemplate template;
//
//    public void convertAndSendMessage(String type,
//                                      Long roomId,
//                                      Long userId,
//                                      String message) {
//        template.convertAndSend(
//                "/subscription/chat/room/" + roomId,
//                new MessageResponseDto(
//                        MessageIdGenerator.generateId(),
//                        type,
//                        "사용자 " + userId + ": " + message
//                )
//        );
//    }
//
//}
