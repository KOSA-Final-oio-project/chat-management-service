package com.oio.memberservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class MemberRequestDto {

    private String email;
    private String name;
    private String password;
    private String profile;
    private String phone;
    private String nickname;
    private String phoneNumber;

}
