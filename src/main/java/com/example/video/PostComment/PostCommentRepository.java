package com.example.video.PostComment;

import com.example.video.Post.Post;
import com.example.video.VideoComment.VideoComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    List<PostComment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Integer postId);
    List<PostComment> findByParentIdOrderByCreatedAtAsc(Integer parentId);


    List<PostComment> findByPostIdOrderByGroupIdAscDepthAscCreatedAtAsc(Integer postId);
    List<PostComment> findByPostIdOrderByGroupIdAscCreatedAtAsc(Integer postId);
    List<PostComment> findByPostIdOrderByCreatedAtAsc(Integer PostId);
    List<PostComment> findAllByPost(Post post);
}
