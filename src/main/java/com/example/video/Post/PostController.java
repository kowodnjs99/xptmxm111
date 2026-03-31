package com.example.video.Post;



import com.example.video.Member.Member;
import com.example.video.Member.MemberDetails;
import com.example.video.PostComment.PostComment;
import com.example.video.PostComment.PostCommentService;
import com.example.video.VideoComment.VideoComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostCommentService postCommentService;


    @GetMapping("/list")
    public String list(
            @PageableDefault(size=9, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model
    ){
        Page<Post> page = postService.getPage(pageable, keyword);

        model.addAttribute("page", page);      // 페이징 객체
        model.addAttribute("keyword", keyword); // 검색어 유지용

        return "post/list";
    }

    @GetMapping("/view/{id}")
    public String view(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("id")int id, Model model
    ) {
        Post post = postService.view(id);
        if(post == null) {
            model.addAttribute("errorCode", "error0002");
            model.addAttribute("errorMsg", "error0002");
            return "error/error";
        }
        model.addAttribute("post", post);

        List<PostComment> PostComment =
                postCommentService.getCommentsHierarchy(id);

        model.addAttribute("PostComment", PostComment);



        return "post/view";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chuga")
    public String chuga(){
        return "/post/chuga";
    }

    @GetMapping("/sakje/{id}")
    public String sakje(
            @PathVariable("id")int id,
            Model model
    ){
        Post post = postService.view(id);
        if(post == null) {
            model.addAttribute("errorCode", "error0002");
            model.addAttribute("errorMsg", "error0002");
            return "error/error";
        }
        model.addAttribute("post", post);
        return "post/sakje";
    }

    @GetMapping("/sujung/{id}")
    public String sujung(
            @PathVariable("id")int id,
            Model model
    ){
        Post post = postService.view(id);
        if(post == null) {
            model.addAttribute("errorCode", "error0002");
            model.addAttribute("errorMsg", "error0002");
            return "error/error";
        }
        model.addAttribute("post", post);
        return "post/sujung";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chugaProc")
    public String chugaProc(
            @AuthenticationPrincipal MemberDetails memberDetails,
            PostDTO postDTO
    ){
        Member loginMember = null;

        if (memberDetails != null) {
            loginMember = memberDetails.getMember();           // ✅ Member 꺼내기
        }
        postService.chugaProc(postDTO, loginMember);              // ✅ writer 넘겨줌
        return "redirect:/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/sakjeProc")
    public String sakjeProc(
            @PathVariable("id")int id
    ){
        postService.sakjeProc(id);
        return "post/sakjeProc";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/sujungProc")
    public String sujungProc(
            PostDTO postDTO
    ){
        postService.sujungProc(postDTO);
        return "post/sujungProc";
    }















}
