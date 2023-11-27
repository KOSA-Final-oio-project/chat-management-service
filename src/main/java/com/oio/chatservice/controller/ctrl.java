package com.oio.chatservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/member-service")
@RestController
public class ctrl {

    @GetMapping("/member")
    String home() {
        return "멤버입니다";
    }

}
