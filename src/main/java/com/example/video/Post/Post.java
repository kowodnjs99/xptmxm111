package com.example.video.Post;

import com.example.video.BaseEntity;
import com.example.video.Member.Member;
//import com.example.video.PostComment.PostComment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 제목
    @Column(nullable = false, length = 200)
    private String subject;

    // 내용
    @Lob
    @Column(nullable = false)
    private String content;

//    // 작성자 이름 (회원 기능 없으면 String 사용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    // 조회수
    private int viewCount = 0;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PostComment> comments = new ArrayList<>();




/*
특징 설명
✔ 1) subject

자유 게시판 제목 → NOT NULL

✔ 2) content

내용이 길 수 있으니 @Lob 사용

✔ 3) writer

회원 시스템이 없다면 문자열로 저장하는 방식이 가장 단순

✔ 4) 조회수

기본값 0

✔ 5) 등록/수정 날짜 auto 처리

@CreationTimestamp, @UpdateTimestamp








*/


}