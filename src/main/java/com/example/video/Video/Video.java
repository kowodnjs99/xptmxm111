package com.example.video.Video;

import com.example.video.BaseEntity;
import com.example.video.Member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "videos")
public class Video  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Lob
    private String content; // 게시물 내용

    @Column(nullable = false, length = 500)
    private String youtubeUrl; // 유튜브 링크

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    // 조회수
    private int viewCount;
    private int likeCount;

//    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<VideoComment> comments = new ArrayList<>();

}