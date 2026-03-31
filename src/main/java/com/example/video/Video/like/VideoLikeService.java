package com.example.video.Video.like;

import com.example.video.Member.Member;
import com.example.video.Video.Video;
import com.example.video.Video.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoLikeService {

    private final VideoRepository videoRepository;
    private final VideoLikeRepository videoLikeRepository;

    public boolean toggle(int videoId, Member member) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow();

        Optional<VideoLike> like =
                videoLikeRepository.findByVideoAndMember(video, member);

        if (like.isPresent()) {
            videoLikeRepository.delete(like.get());
            video.setLikeCount(video.getLikeCount() - 1);
            return false;
        } else {
            videoLikeRepository.save(new VideoLike(video, member));
            video.setLikeCount(video.getLikeCount() + 1);
            return true;
        }
    }

    // ✅ 컨트롤러에서 이 메서드만 호출
    public boolean toggleLike(int videoId, Member member) {

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("영상 없음"));

        return toggleLike(video, member);
    }

    // ✅ 실제 로직 담당 (서비스 내부 전용)
    private boolean toggleLike(Video video, Member member) {

        return videoLikeRepository.findByVideoAndMember(video, member)
                .map(like -> {
                    videoLikeRepository.delete(like);
                    video.setLikeCount(video.getLikeCount() - 1);
                    return false;
                })
                .orElseGet(() -> {
                    VideoLike like = VideoLike.builder()
                            .video(video)
                            .member(member)
                            .build();

                    videoLikeRepository.save(like);
                    video.setLikeCount(video.getLikeCount() + 1);
                    return true;
                });
    }


}
