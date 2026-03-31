package com.example.video.Video.view;

import com.example.video.Member.Member;
import com.example.video.Video.Video;
import com.example.video.Video.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoViewService {

    private final VideoRepository videoRepository;
    private final VideoViewRepository videoViewRepository;

    private void increaseViewCount(Video video, Member member) {

        // 비로그인 사용자 → 단순 증가
        if (member == null) {
            video.setViewCount(video.getViewCount() + 1);
            return;
        }

        boolean alreadyViewed =
                videoViewRepository.existsByVideoAndMember(video, member);

        if (alreadyViewed) {
            return;
        }

        VideoView view = VideoView.builder()
                .video(video)
                .member(member)
                .build();

        videoViewRepository.save(view);
        video.setViewCount(video.getViewCount() + 1);
    }


    public void increaseViewCount(int videoId, Member member) {

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("영상 없음"));

        increaseViewCount(video, member);
    }

}
