package com.example.video.Video;

import com.example.video.Member.Member;
import com.example.video.Member.MemberDetails;
import com.example.video.Video.view.VideoViewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.video.Video.InvalidYoutubeUrlException;

import com.example.video.Member.MemberService;
import com.example.video.VideoComment.VideoComment;
import com.example.video.VideoComment.VideoCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/video")
@Controller
public class VideoController {
    private final VideoViewService videoViewService;
    private final VideoService videoService;
    private final VideoCommentService videoCommentService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(
            @PageableDefault(size=9, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model
    ) {
        Page<Video> page = videoService.getPage(pageable, keyword);

        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        return "video/list";
    }

    @GetMapping("/view/{id}")
    public String view(
            Model model,
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal Member member
    ) {
        Video video = videoService.view(id);
        if (video == null) {
            model.addAttribute("errorCode", "error0002");
            model.addAttribute("errorMsg", "error0002");
            return "error/error";
        }

        // ✅ 조회수 증가
        videoViewService.increaseViewCount(id, member);
        model.addAttribute("video", video);

        List<VideoComment> VideoComment =
                videoCommentService.getCommentsHierarchy(id);

        model.addAttribute("VideoComment", VideoComment);

        return "video/view";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chuga")
    public String chuga(Model model) {
        // 폼 바인딩용 DTO
        model.addAttribute("videoDTO", new VideoDTO());
        return "video/chuga";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chugaProc")
    public String chugaProc(
            @Valid @ModelAttribute("videoDTO") VideoDTO videoDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        Member loginMember = null;

        if (memberDetails != null) {
            loginMember = memberDetails.getMember();           // ✅ Member 꺼내기
        }


        if (bindingResult.hasErrors()) {
            return "video/chuga";
        }

        try {
            String videoId = videoService.extractYoutubeId(videoDTO.getYoutubeUrl());
            videoDTO.setYoutubeUrl(videoId);
            videoService.chugaProc(videoDTO, loginMember);// ✅ writer 넘겨줌

        } catch (InvalidYoutubeUrlException e) {
            bindingResult.rejectValue(
                    "youtubeUrl",
                    "invalid.youtubeUrl",
                    e.getMessage()
            );
            return "video/chuga";
        }

        return "redirect:/video/list";
    }


    @GetMapping("/sujung/{id}")
    public String sujung(
            Model model,
            @PathVariable("id")Integer id
    ) {
        VideoDTO videoDTO = videoService.getVideoDTO(id);
        model.addAttribute("videoDTO", videoDTO);

        Video video = videoService.view(id);
        if (video == null) {
            model.addAttribute("errorCode", "error0002");
            model.addAttribute("errorMsg", "error0002");
            return "/error/error";
        }
        model.addAttribute("video",video);
        return "video/sujung";
    }

    @PostMapping("/sujungProc")
    public String sujungProc(
            @Valid @ModelAttribute("videoDTO") VideoDTO videoDTO,
            BindingResult bindingResult
    ) {
        // 1차: 제목/내용/URL 공백/길이 검증
        if (bindingResult.hasErrors()) {
            return "video/sujung";
        }

        try {
            // 유튜브 ID 추출
            String videoId = videoService.extractYoutubeId(videoDTO.getYoutubeUrl());
            videoDTO.setYoutubeUrl(videoId);

            // 수정 처리
            videoService.sujungProc(videoDTO);

        } catch (InvalidYoutubeUrlException e) {
            // 유튜브 URL이 이상한 경우: 필드 에러로 달고 다시 폼으로
            bindingResult.rejectValue(
                    "youtubeUrl",
                    "invalid.youtubeUrl",
                    e.getMessage()
            );
            return "video/sujung";
        }

        // 수정 후 해당 글 상세 페이지로 이동 (경로는 네 프로젝트에 맞게)
        return "redirect:/video/view/" + videoDTO.getId();
    }


    @GetMapping("/sakje/{id}")
    public String sakje(
            Model model,
            @PathVariable("id")Integer id

    ) {
        Video video = videoService.view(id);
        if (video == null) {
            model.addAttribute("errorCode", "error0002");
            model.addAttribute("errorMsg", "error0002");
            return "error/error";
        }
        model.addAttribute("video",video);
        return "video/sakje";
    }


    @PostMapping("/sakjeProc")
    public String sakjeProc(
            Model model,
            VideoDTO videoDTO   ) {


        Video video = videoService.view2(videoDTO);
        if (video == null) {
            model.addAttribute("errorCode", "error0003");
            model.addAttribute("errorMsg", "error0003");
            return "error/error";
        }
        videoDTO.setId(video.getId());
        videoService.sakjeProc(videoDTO);
        return"redirect:/video/list";
    }

}





