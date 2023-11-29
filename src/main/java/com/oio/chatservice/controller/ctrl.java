package com.oio.chatservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chat-service")
@RestController
public class ctrl {

    @GetMapping("/chat")
    String home() {
        return "멤버입니다";
    }

}
