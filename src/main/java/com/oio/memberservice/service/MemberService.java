package com.oio.memberservice.service;

import com.oio.memberservice.dto.MemberRequestDto;
import com.oio.memberservice.jpa.MemberEntity;
import com.oio.memberservice.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public void createMember(MemberRequestDto memberRequestDto) throws UnsupportedEncodingException {
        
        MemberEntity member = mapper.map(memberRequestDto, MemberEntity.class);
        member.changeEncryptedPwd(passwordEncoder.encode(memberRequestDto.getPassword()));
        member.changeStatusToBasic();
        member.changeJoinDate(LocalDateTime.now());

        memberRepository.save(member);
    }

    public String idDupChk(String email) {
        Optional<MemberEntity> result = memberRepository.findByEmail(email);

        if(result.isPresent()){
            return "중복";
        }
        return "사용가능";
    }
}
