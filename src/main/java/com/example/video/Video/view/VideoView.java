package com.example.video.Video.view;

import com.example.video.Member.Member;
import com.example.video.Video.Video;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(
        name = "video_views",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"video_id", "member_id"}
        )
)
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public VideoView(Video video, Member member) {
        this.video = video;
        this.member = member;
    }
}
