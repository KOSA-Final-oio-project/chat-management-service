package com.oio.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat-service")
@RequiredArgsConstructor
public class ChatController {

    /*
    @MessageMapping("/message")
    public void receiveMessage(@Payload ChattingMessage message) {
        //TODO
    }
    */

}
