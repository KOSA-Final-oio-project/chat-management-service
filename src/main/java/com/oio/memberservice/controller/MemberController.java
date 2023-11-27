package com.oio.memberservice.controller;

import com.oio.memberservice.dto.MemberRequestDto;
import com.oio.memberservice.dto.emailChkDto;
import com.oio.memberservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member-service")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signUp(@RequestBody MemberRequestDto memberRequestDto){
        Map result = new HashMap();
        try {
            memberService.createMember(memberRequestDto);
            result.put("status","success");
        }catch (UnsupportedEncodingException e){
            result.put("status","error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/idchk")
    public String idDupChk(@RequestBody emailChkDto emailChkDto){
        String result = memberService.idDupChk(emailChkDto.getEmail());

        return result;
    }


}
