package com.example.video.Video;


import com.example.video.Member.Member;
import com.example.video.Video.InvalidYoutubeUrlException;
import jakarta.persistence.EntityNotFoundException;


import com.example.video.VideoComment.VideoCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    private final VideoCommentRepository videoCommentRepository;

    public List<Video> list(){
        return videoRepository.findAll();
    }

    // dualList, 메인 화면 등에서 "최근 영상 몇 개" 보여줄 때 쓰는 리스트용
    public List<Video> getList() {
        // 필요하면 정렬 추가
        return videoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 영상 게시판 리스트 + 검색 + 페이징 통합 메서드
     */
    public Page<Video> getPage(Pageable pageable, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return videoRepository.findAll(pageable);
        }
        return videoRepository.findByTitleContainingIgnoreCase(keyword.trim(), pageable);
    }

    // (선택) 통합 검색에서 쓰는 리스트용
    public List<Video> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        return videoRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }



    public Video view(int id){
        Video video = null;
        Optional<Video> ov = videoRepository.findById(id);
        if(ov.isPresent()){
            video =  ov.get();
        }
        return video;
    }

    public Video view2(VideoDTO videoDTO){
        Video video = null;
        Optional<Video> ov = videoRepository.findById(videoDTO.getId());
        if(ov.isPresent()){
            video =  ov.get();
        }
        return video;
    }


    public void chugaProc(VideoDTO videoDTO, Member writer){
        Video video = new Video();
        video.setTitle(videoDTO.getTitle());
        video.setContent(videoDTO.getContent());
        video.setYoutubeUrl(videoDTO.getYoutubeUrl());
        video.setWriter(writer);
        videoRepository.save(video);
    }

    // ✅ 수정 폼에서 사용할 DTO 조회용
    @Transactional(readOnly = true)
    public VideoDTO getVideoDTO(int id) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("영상이 존재하지 않습니다. id=" + id));

        // 🔹 수동 매핑 (modelMapper 안 씀)
        VideoDTO dto = new VideoDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setContent(video.getContent());
        dto.setYoutubeUrl(video.getYoutubeUrl());

        return dto;
    }


    @Transactional
    public void sujungProc(VideoDTO videoDTO){

        // 1. id 없으면 수정 불가
        if (videoDTO.getId() == null) {
            throw new IllegalArgumentException("수정하려는 영상의 id가 없습니다.");
        }
        // 2. 기존 엔티티 조회
        Video video = videoRepository.findById(videoDTO.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("영상이 존재하지 않습니다. id=" + videoDTO.getId())
                );

        // 3. 변경할 필드만 덮어쓰기
        video.setTitle(videoDTO.getTitle());
        video.setContent(videoDTO.getContent());
        video.setYoutubeUrl(videoDTO.getYoutubeUrl()); // 컨트롤러에서 이미 ID 추출해 넣어줬다고 가정

        // 4. save()는 선택이지만, 헷갈리지 않게 명시해도 됨
        videoRepository.save(video);
    }
    public void sakjeProc(VideoDTO videoDTO) {
        Video video = new Video();
        video.setId(videoDTO.getId());
        videoRepository.delete(video);
    }

//    public void addCommentToVideo(int videoId, String content, Member writer) {
//        // 해당 비디오 객체 가져오기
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

//        // 댓글 객체 생성
//        VideoComment videoComment = new VideoComment();
//        videoComment.setContent(content); // 댓글 내용
//        videoComment.setVideo(video); // 해당 비디오와 연결
//        videoComment.setMember(writer); // 댓글 작성자 (로그인한 사용자)
//
//        // 댓글 저장
//        videoCommentRepository.save(videoComment);
//    }

    // 유튜브 영상 ID 추출
    public String extractYoutubeId(String youtubeUrl) {

        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            throw new InvalidYoutubeUrlException("유튜브 주소를 입력해 주세요.");
        }

        String url = youtubeUrl.trim();

        // 1) 짧은 링크 (https://youtu.be/XXXX)
        if (url.contains("youtu.be/")) {
            String id = url.substring(url.lastIndexOf("/") + 1);
            if (id.isEmpty()) {
                throw new InvalidYoutubeUrlException("유효한 유튜브 주소가 아닙니다.");
            }
            return id;
        }

        // 2) 긴 링크 (https://www.youtube.com/watch?v=XXXX&...)
        if (url.contains("watch?v=")) {
            String afterV = url.substring(url.indexOf("watch?v=") + 8); // v= 뒤부터
            int ampIndex = afterV.indexOf("&");
            String id = (ampIndex != -1) ? afterV.substring(0, ampIndex) : afterV;
            if (id.isEmpty()) {
                throw new InvalidYoutubeUrlException("유효한 유튜브 주소가 아닙니다.");
            }
            return id;
        }

        // 3) http 인데 유튜브 형식이 아닌 경우 → 잘못된 주소로 간주
        if (url.startsWith("http")) {
            throw new InvalidYoutubeUrlException("지원하지 않는 유튜브 주소 형식입니다.");
        }

        // 4) 그 외에는 "그냥 ID만 입력한 경우"라고 보고 그대로 사용
        return url;
    }
}




