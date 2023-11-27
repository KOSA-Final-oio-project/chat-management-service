package com.oio.memberservice.jpa;

import com.oio.memberservice.status.MemberStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Entity
@Setter
@Table(name = "member")
public class MemberEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column
    private String profile;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column
    private Date withdrawalDate;

    @Column(nullable = false, unique = true)
        private String encryptedPwd;

    public MemberEntity() {}

    public void changeEncryptedPwd(String encryptedPwd) {
        this.encryptedPwd = encryptedPwd;
    }

    public void changeStatusToBasic() {
        this.status = MemberStatus.일반회원;
    }

    public void changeJoinDate(LocalDateTime now) {
        this.joinDate = now;
    }
}
