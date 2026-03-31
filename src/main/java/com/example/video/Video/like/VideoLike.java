package com.example.video.Video.like;

import com.example.video.Member.Member;
import com.example.video.Video.Video;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(
        name = "video_likes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"video_id", "member_id"}
        )
)
@Getter
@NoArgsConstructor
@Data
@AllArgsConstructor
public class VideoLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public VideoLike(Video video, Member member) {
        this.video = video;
        this.member = member;
    }
}
