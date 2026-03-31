package com.example.video.VideoComment;

import com.example.video.Member.Member;
import com.example.video.Member.MemberDetails;
import com.example.video.Member.MemberService;
import com.example.video.Video.Video;
import com.example.video.Video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;




@Controller
@RequiredArgsConstructor
@RequestMapping("/video/comment")
public class VideoCommentController {

    // 지금은 직접 안 쓸 수도 있지만, 확장할 때 쓰려고 남겨둔 필드들
    private final VideoService videoService;
    private final VideoCommentService videoCommentService;
    private final MemberService memberService;


    /**
    * ✅ 댓글/대댓글 등록
    * - 로그인한 사용자(MemberDetails 기준)
    * - parentId 가 있으면 대댓글, 없으면 일반 댓글
    */
// 댓글 작성 (POST 요청)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public String add(
            @RequestParam int videoId,
            @RequestParam String content,
            @RequestParam(required = false) Integer parentId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        // 혹시 모를 안전장치 (PreAuthorize 덕분에 사실상 올 일 거의 없음)
        if (memberDetails == null) {
            return "redirect:/member/login";
        }

        videoCommentService.addComment(
                videoId,
                content,
                memberDetails.getMember(),
                parentId
        );
        // 4️⃣ 다시 영상 상세 페이지로
        return "redirect:/video/view/" + videoId;
    }
    /**
     * ✅ 댓글 삭제
     * - 댓글이 실제로 존재하는지 먼저 확인
     * - 존재하지 않으면 에러 페이지로
     * - 존재하면 서비스의 deleteComment(commentId) 호출
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{commentId}")
    public String delete(
            Model model,
            @PathVariable("commentId") int commentId
    ){
        // 학습용으로 남겨둔 view(...)를 이용해서 댓글 먼저 조회
        VideoComment videoComment = videoCommentService.view(commentId);
        if(videoComment == null){
            model.addAttribute("errorCode", "err0002");
            model.addAttribute("errorMsg", "해당 코멘트는 존재하지 않습니다.");
            return "error/error";
        }
        int videoId = videoComment.getVideo().getId(); // 리다이렉트용 영상 ID

        // ✅ 실제 삭제는 새 deleteComment() 사용
//        videoCommentService.sakjeProc(videoComment);
        videoCommentService.deleteComment(commentId);
        return "redirect:/video/view/" + videoComment.getVideo().getId();
    }
    /**
     * ✅ 댓글 수정
     * - Principal.getName() == 댓글 작성자 username 인지 서비스에서 체크
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/edit")
    public String editComment(
            @RequestParam int commentId,
            @RequestParam int videoId,
            @RequestParam String content,
            Principal principal
    ) {
        if (principal == null) {
            // 로그인 페이지로 리다이렉트 또는 예외 처리
            return "redirect:/login";
        }

        // 서비스에서 "내 댓글인지" 검사 + 내용 수정
        videoCommentService.edit(commentId, content, principal.getName());

        return "redirect:/video/view/" + videoId;
    }


}
