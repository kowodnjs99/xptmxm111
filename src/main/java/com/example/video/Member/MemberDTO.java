package com.example.video.Member;

import com.example.video.config.MemberStatus;
import com.example.video.config.RoleType;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
public class MemberDTO {

    private int id;
    //@NotBlank
    private String username;

    private String password;

    private int viewCount; // 조회수

    private RoleType role; // USER, ADMIN 등

    private String email; // 선택이지만 권장

    private String nickname; // 화면용 표시 이름

    private LocalDateTime createDate; //등록날짜

    private LocalDateTime updateDate; //수정한 날짜

    private MemberStatus status; // ACTIVE / INACTIVE 멤버 상태
}
