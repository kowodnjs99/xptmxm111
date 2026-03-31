package com.example.video.PostComment;

import com.example.video.Member.Member;
import com.example.video.Post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /* 댓글 내용 */
    @Lob
    @Column(nullable = false)
    private String content;

    /* 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /* 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /* ================= 계층형 댓글 핵심 ================= */

    /* 부모 댓글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    /* 자식 댓글 */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> children = new ArrayList<>();

    /* 들여쓰기 깊이 */
    @Column(nullable = false)
    private int depth;

    /* 최상위 댓글 그룹 */
    @Column(nullable = false)
    private int groupId;

    /* ================= 생성 메서드 ================= */

    public static PostComment create(
            String content,
            Post post,
            Member member,
            PostComment parent
    ) {
        PostComment c = new PostComment();
        c.content = content;
        c.post = post;
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

}
