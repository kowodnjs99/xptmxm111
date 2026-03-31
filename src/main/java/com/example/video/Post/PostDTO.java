package com.example.video.Post;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {

    private int id;

    // 제목
    private String subject;

    // 내용
    private String content;

    // 작성자 이름 (회원 기능 없으면 String 사용)
    private Integer writerId;

    // 조회수
    private int viewCount = 0;

    // 등록일시
    private LocalDateTime createDate;

    // 수정일시
    private LocalDateTime updateDate;
}
