package com.example.video.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {


    // (1) 제목 검색 + 페이징용
    Page<Post> findBySubjectContainingIgnoreCase(String keyword, Pageable pageable);

    // (2) 제목 검색 + 리스트용 (혹시 기존 코드에서 쓰고 있으면 그대로 둬도 됨)
    List<Post> findBySubjectContainingIgnoreCase(String keyword);

    List<Post> findAllByOrderByIdDesc();


}

