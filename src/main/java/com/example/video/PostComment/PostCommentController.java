package com.example.video.PostComment;

import com.example.video.Member.MemberDetails;
import com.example.video.Post.Post;
import com.example.video.Post.PostRepository;
import com.example.video.Post.PostService;
import com.example.video.Video.Video;
import com.example.video.Video.VideoRepository;
import com.example.video.VideoComment.VideoComment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post/comment")
public class PostCommentController {

    private final PostService postService;
    private final PostCommentService postCommentService;

    // 댓글 작성 (POST 요청)
    @PostMapping("/add")
    public String add(
        @RequestParam int postId,
        @RequestParam String content,
        @RequestParam(required = false) Integer parentId,
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
            // 로그인 안 했으면 차단
            if (memberDetails == null) {
                return "redirect:/member/login";
            }

            postCommentService.addComment(
                    postId,
                    content,
                    memberDetails.getMember(),
                    parentId
            );

            // 게시글 상세 페이지로 복귀
            return "redirect:/post/view/" + postId;
    }


    @PostMapping("/delete/{commentId}")
    public String delete(
            Model model,
            @PathVariable("commentId") int commentId
    ){
        PostComment PostComment = postCommentService.view(commentId);
        if(PostComment == null){
            model.addAttribute("errorCode", "err0002");
            model.addAttribute("errorMsg", "해당 코멘트는 존재하지 않습니다.");
            return "error/error";
        }

        postCommentService.sakjeProc(PostComment);
        return "redirect:/post/view/" + PostComment.getPost().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/edit")
    public String editComment(
            @RequestParam int commentId,
            @RequestParam int postId,
            @RequestParam String content,
            Principal principal
    ) {
        if (principal == null) {
            // 로그인 페이지로 리다이렉트 또는 예외 처리
            return "redirect:/login";
        }
        postCommentService.edit(commentId, content, principal.getName());
        return "redirect:/post/view/" + postId;
    }


}
