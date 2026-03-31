package com.example.video.VideoComment;

import java.util.List;

import com.example.video.Post.Post;
import com.example.video.PostComment.PostComment;
import com.example.video.Video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoCommentRepository extends JpaRepository<VideoComment, Integer>{
    List<VideoComment> findByVideoIdOrderByGroupIdAscDepthAscCreatedAtAsc(Integer videoId);

    List<VideoComment> findByVideoIdOrderByGroupIdAscCreatedAtAsc(Integer videoId);

    List<VideoComment> findByVideoIdAndParentIsNullOrderByCreatedAtDesc(Integer videoId);
    List<VideoComment> findByParentIdOrderByCreatedAtAsc(Integer parentId);


    List<PostComment> findAllByVideo(Video video);
    List<VideoComment> findByVideoIdOrderByCreatedAtAsc(Integer videoId);
}
