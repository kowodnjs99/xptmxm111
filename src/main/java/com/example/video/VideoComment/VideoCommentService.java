package com.example.video.VideoComment;

import com.example.video.Member.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.video.Member.MemberRepository;
import com.example.video.Video.Video;
import com.example.video.Video.VideoRepository;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class VideoCommentService {


    private final VideoCommentRepository videoCommentRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository; // 🔹 지금은 직접 안 쓰지만, 확장 여지용


    // =========================================
    // [현재 사용 중인 핵심 댓글/대댓글 기능]
    // =========================================

    /**
     * ✅ 최종 버전: 정렬 튜닝이 끝난 댓글 목록 조회
     * - groupId, depth, createdAt 기준 정렬
     * - 대댓글까지 포함해서 화면에 뿌릴 때 사용
     */

    @Transactional(readOnly = true)
    public List<VideoComment> getComments(int videoId) {
        return videoCommentRepository
                .findByVideoIdOrderByGroupIdAscDepthAscCreatedAtAsc(videoId);
    }


    /**
     * ✅ 최종 버전: 댓글 등록 (대댓글 포함)
     * @param videoId  대상 영상 id
     * @param content  댓글 내용
     * @param member   작성자 Member (로그인한 유저)
     * @param parentId 부모 댓글 id (없으면 null = 최상위 댓글)
     */
    public void addComment(
            int videoId,
            String content,
            Member member,
            Integer parentId
    ) {
        Video video = videoRepository.findById(videoId).orElseThrow();

        VideoComment comment = new VideoComment();
        comment.setVideo(video);
        comment.setContent(content);
        comment.setMember(member);

        // 🔹 대댓글인 경우: 부모 기준으로 groupId / depth 세팅
        if (parentId != null) {
            VideoComment parent = videoCommentRepository.findById(parentId).orElseThrow();
            comment.setParent(parent);
            comment.setGroupId(parent.getGroupId());
            comment.setDepth(parent.getDepth() + 1);
        } else {
            // 🔹 최상위 댓글
            comment.setDepth(0);
        }

        // 1️⃣ 먼저 저장 → id 생성
        videoCommentRepository.save(comment);

        // 2️⃣ 최상위 댓글이면 자기 자신이 group
        if (parentId == null) {
            comment.setGroupId(comment.getId());
            videoCommentRepository.save(comment);
        }
    }

    /**
     * ✅ 댓글 수정
     * - 작성자 username 이 일치할 때만 수정 가능
     */
    @Transactional
    public void edit(int commentId, String content, String username) {

        VideoComment comment = videoCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        if (!comment.getMember().getUsername().equals(username)) {
            throw new AccessDeniedException("수정 권한 없음");
        }

        comment.setContent(content);
    }








    /**
     * ✅ 댓글 삭제
     * - 지금은 “대댓글 있든 말든 그냥 삭제” 정책
     *   (정책 바꾸고 싶으면 여기만 손대면 됨)
     */
    // 댓글 삭제 (대댓글 없으니 단순 삭제)
    public void deleteComment(int commentId) {
        Optional<VideoComment> ov = videoCommentRepository.findById(commentId);
        VideoComment videoComment = new VideoComment();
        videoComment.setId(ov.get().getId());
        videoCommentRepository.delete(videoComment);
    }

    // =================================================================
    // [LEARNING NOTE] 과거에 쓰던 버전들 / 정렬 실험 / 삭제 연습용 메서드
    // - 지금은 컨트롤러에서 직접 호출하지 않는 “연습 흔적”용 코드
    // - 필요하면 참고만 하고, 실제 화면용으로는 위의 메서드들을 사용
    // =================================================================



    /**
     * ⛔ 옛날 버전 1: 단순 createdAt 오름차순
     */
    @Transactional(readOnly = true)
    public List<VideoComment> getCommentsForVideo(Integer videoId) {
        return videoCommentRepository.findByVideoIdOrderByCreatedAtAsc(videoId);
    }

    /**
     * ⛔ 옛날 버전 2: Optional 연습용 단건 조회
     */
    public VideoComment view(int commentId) {
        Optional<VideoComment> oc = videoCommentRepository.findById(commentId);
        VideoComment videoComment = null;
        if (oc.isPresent()) {
            videoComment = oc.get();
        }
        return videoComment;
    }

    /**
     * ⛔ 옛날 버전 3: 엔티티를 그대로 받아서 삭제하는 연습용 메서드
     */
    public void sakjeProc(VideoComment videoComment) {

        videoCommentRepository.delete(videoComment);
    }

    /**
     * ⛔ 정렬 실험용: 부모 최신순 → 자식 오래된순으로 나열
     * - “최신 댓글이 위에, 그 밑에 대댓글들” 형태 실험할 때 썼던 버전
     */
    public List<VideoComment> getCommentsHierarchy(int videoId) {

        List<VideoComment> result = new ArrayList<>();

        // 1️⃣ 최상위 댓글 (최신순)
        List<VideoComment> parents =
                videoCommentRepository
                        .findByVideoIdAndParentIsNullOrderByCreatedAtDesc(videoId);

        for (VideoComment parent : parents) {
            result.add(parent);

            // 2️⃣ 대댓글 (부모 아래)
            List<VideoComment> children =
                    videoCommentRepository
                            .findByParentIdOrderByCreatedAtAsc(parent.getId());

            result.addAll(children);
        }

        return result;
    }

    /**
     * ⛔ 계층 구조 + 재귀로 children 펼치는 실험용 버전
     */
    public List<VideoComment> getHierarchicalComments(int videoId) {

        List<VideoComment> all =
                videoCommentRepository
                        .findByVideoIdOrderByGroupIdAscCreatedAtAsc(videoId);

        List<VideoComment> result = new ArrayList<>();

        for (VideoComment comment : all) {
            if (comment.getParent() == null) {
                result.add(comment);
                addChildren(comment, result);
            }
        }
        return result;
    }

    /**
     * ⛔ 재귀 헬퍼: 위 실험용 메서드(getHierarchicalComments)에서만 사용됨
     */
    private void addChildren(VideoComment parent, List<VideoComment> result) {
        for (VideoComment child : parent.getChildren()) {
            result.add(child);
            addChildren(child, result);
        }
    }

}