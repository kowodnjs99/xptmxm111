package com.example.video.VideoComment;

import com.example.video.Member.Member;
import com.example.video.Video.Video;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="VideoComments")

public class VideoComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /* 🔁 부모 댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private VideoComment parent;

    /* 자식 */
    @OneToMany(mappedBy = "parent")
    private List<VideoComment> children = new ArrayList<>();

    /* ⭐ 핵심 */
    @Column(nullable = false)
    private int depth;

    @Column(nullable = false)
    private int groupId;

    /* 생성 메서드 */
    public static VideoComment create(
            String content,
            Video video,
            Member member,
            VideoComment parent
    ) {
        VideoComment c = new VideoComment();
        c.content = content;
        c.video = video;
        c.member = member;
        c.parent = parent;

        if (parent == null) {
            c.depth = 0;
        } else {
            c.depth = parent.depth + 1;
            c.groupId = parent.groupId;
        }

        return c;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
