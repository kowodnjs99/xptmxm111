package com.example.video.Video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findAllByOrderByIdDesc();

    // (1) 제목 검색 + 페이징용
    Page<Video> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // (2) 제목 검색 + 리스트용 (기존 검색 로직에서 쓴다면 유지)
    List<Video> findByTitleContainingIgnoreCase(String keyword);

}
