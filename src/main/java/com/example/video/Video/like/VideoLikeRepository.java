package com.example.video.Video.like;


import com.example.video.Member.Member;
import com.example.video.Video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Integer> {

    Optional<VideoLike> findByVideoAndMember(Video video, Member member);

    boolean existsByVideoAndMember(Video video, Member member);
}