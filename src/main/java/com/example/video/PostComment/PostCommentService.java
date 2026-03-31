package com.example.video.PostComment;


import com.example.video.Member.Member;
import com.example.video.Post.Post;
import com.example.video.Post.PostRepository;
import com.example.video.Video.Video;
import com.example.video.VideoComment.VideoComment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public List<PostComment> getCommentsForVideo(Integer postId) {
        return postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }


    // 댓글 등록
     public void addComment(
            int postId,
            String content,
            Member member,
            Integer parentId
     ) {
         Post post = postRepository.findById(postId).orElseThrow();

         PostComment comment = new PostComment();
         comment.setPost(post);
         comment.setContent(content);
         comment.setMember(member);

         if (parentId != null) {
             PostComment parent = postCommentRepository.findById(parentId).orElseThrow();
             comment.setParent(parent);
             comment.setGroupId(parent.getGroupId());
             comment.setDepth(parent.getDepth() + 1);
         } else {
             comment.setDepth(0);
         }

         postCommentRepository.save(comment);

         if (parentId == null) {
             comment.setGroupId(comment.getId());
             postCommentRepository.save(comment);
         }
     }
    public List<PostComment> getCommentsHierarchy(int postId) {

        List<PostComment> result = new ArrayList<>();

        // 1️⃣ 최상위 댓글 (최신순)
        List<PostComment> parents =
                postCommentRepository
                        .findByPostIdAndParentIsNullOrderByCreatedAtDesc(postId);

        for (PostComment parent : parents) {
            result.add(parent);

            // 2️⃣ 대댓글 (부모 아래)
            List<PostComment> children =
                    postCommentRepository
                            .findByParentIdOrderByCreatedAtAsc(parent.getId());

            result.addAll(children);
        }

        return result;
    }


    public List<PostComment> getHierarchicalComments(int postId) {

        List<PostComment> all =
                postCommentRepository
                        .findByPostIdOrderByGroupIdAscCreatedAtAsc(postId);


        List<PostComment> result = new ArrayList<>();

        for (PostComment comment : all) {
            if (comment.getParent() == null) {
                result.add(comment);
                addChildren(comment, result);
            }
        }
        return result;
    }

    private void addChildren(PostComment parent, List<PostComment> result) {
        for (PostComment child : parent.getChildren()) {
            result.add(child);
            addChildren(child, result);
        }
    }


    public List<PostComment> getComments(int postId) {
        return postCommentRepository
                .findByPostIdOrderByGroupIdAscDepthAscCreatedAtAsc(postId);
    }

    public List<PostComment> VCview(int postId) {
        return postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public PostComment view(int  commentId) {
        Optional<PostComment> oc = postCommentRepository.findById(commentId);
        PostComment postComment = null;
        if (oc.isPresent()) {
            postComment = oc.get();
        }
        return postComment;
    }
    // 댓글 삭제 (대댓글 없으니 단순 삭제)
    public void deleteComment(Integer commentId) {
        Optional<PostComment> ov = postCommentRepository.findById(commentId);
        PostComment PostComment = new PostComment();
        PostComment.setId(ov.get().getId());
        postCommentRepository.delete(PostComment);
    }

    public void sakjeProc(PostComment PostComment) {
        postCommentRepository.delete(PostComment);
    }


    @Transactional
    public void edit(int commentId, String content, String username) {

        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        if (!comment.getMember().getUsername().equals(username)) {
            throw new AccessDeniedException("수정 권한 없음");
        }

        comment.setContent(content);
    }

}
