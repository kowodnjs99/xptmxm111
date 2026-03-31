package com.example.video.Post;


import com.example.video.Member.Member;
import com.example.video.Member.MemberRepository;
//import com.example.video.PostComment.PostComment;
//import com.example.video.PostComment.PostCommentRepository;
import com.example.video.Video.Video;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    @Autowired
    private MemberRepository memberRepository;

    private final PostRepository postRepository;
//    private final PostCommentRepository postCommentRepository;

    // [dualList] 등에서 쓰는 기존 List용 메서드는 그대로 유지
    public List<Post> getList() {
        // 정렬은 필요에 맞게 설정
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 자유 게시판 리스트 + 검색 + 페이징 통합 메서드
     *
     * - keyword가 비어있으면: 일반 리스트 페이징
     * - keyword가 있으면: 제목 검색 결과 페이징
     */
    public Page<Post> getPage(Pageable pageable, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            // 검색어 없으면 전체 목록 페이징
            return postRepository.findAll(pageable);
        }
        // 검색어 있으면 제목 검색 + 페이징
        return postRepository.findBySubjectContainingIgnoreCase(keyword.trim(), pageable);
    }

    // (선택) 통합 검색에서만 쓸 리스트용 검색 (원래 있던 거라면 유지 가능)
    public List<Post> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        return postRepository.findBySubjectContainingIgnoreCase(keyword.trim());
    }


    public List<Post> list(){
        return postRepository.findAll();
    }

    public Post view(int id){
        Post post = null;
        Optional<Post> op = postRepository.findById(id);
        if(op.isPresent()){
            post =  op.get();
        }
        return post;
    }

//    public List<PostComment> getPostComments(Post post) {
//        return postCommentRepository.findAllByPost(post);
//    }

    public Post getPost(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
    }

    public void chugaProc(PostDTO postDTO, Member writer){
        Post post = new Post();
        post.setSubject(postDTO.getSubject());
        post.setContent(postDTO.getContent());
        post.setViewCount(0);
        post.setWriter(writer);
        postRepository.save(post);
    }


    @Transactional
    public void sujungProc(PostDTO postDTO) {

        // 1) 기존 글 찾기 (없으면 예외)
        Post post = postRepository.findById(postDTO.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("게시글을 찾을 수 없습니다. id=" + postDTO.getId()));

        // 2) 수정할 내용 덮어쓰기
        post.setSubject(postDTO.getSubject());
        post.setContent(postDTO.getContent());
        post.setViewCount(postDTO.getViewCount());  // 필요 없으면 이 줄은 지워도 됨

        // 3) 변경 내용 저장
        postRepository.save(post);
        // @Transactional 이라 사실 save 없이도 flush 되지만, 명시적으로 두어도 됨
    }


    /** 글 삭제 처리 */
    @Transactional
    public void sakjeProc(int id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("게시글을 찾을 수 없습니다. id=" + id));

        postRepository.delete(post);
    }

}
