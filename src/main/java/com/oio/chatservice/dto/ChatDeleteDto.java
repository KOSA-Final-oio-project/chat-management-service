package com.oio.chatservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDeleteDto {

    private String nickname; // 사용자 닉네임
    private boolean isDeleted; // 삭제 여부

}

