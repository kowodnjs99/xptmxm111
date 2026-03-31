package com.example.video.Video.view;


import com.example.video.Member.Member;
import com.example.video.Video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoViewRepository extends JpaRepository<VideoView, Integer> {

    boolean existsByVideoAndMember(Video video, Member member);
}