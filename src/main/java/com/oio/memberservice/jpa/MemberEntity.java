package com.oio.memberservice.jpa;

import lombok.Getter;

import javax.persistence.*;
import java.util.Date;


@Getter
@Entity
@Table(name = "member")
public class MemberEntity {
    @Id
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column
    private String profile;

    @Column(nullable = false)
    private Long status;

    @Column(nullable = false)
    private Date joinDate;

    @Column
    private Date withdrawalDate;

    @Column(nullable = false, unique = true)
    private String encryptedPwd;

}
