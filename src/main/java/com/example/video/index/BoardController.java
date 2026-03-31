package com.example.video.index;



import com.example.video.Post.Post;
import com.example.video.Post.PostService;
import com.example.video.Video.Video;
import com.example.video.Video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
//    private final NoticeService noticeService;
    private final VideoService videoService;
    private final PostService postService;

    @GetMapping("/dualList")
    public String dualBoard(Model model) {

        // 영상 게시판 목록
        List<Video> videoList = videoService.getList();

        // 자유 게시판 목록
        List<Post> postList = postService.getList();

        // 소개 글 (DB에서 가져오든, 일단 하드코딩해도 됨)
        String introText = "우리 카페는 ~~~ 이런 사이트입니다.\n"
                + "자유롭게 글도 쓰고, 영상도 공유해 보세요!";


        model.addAttribute("introText", introText);
        model.addAttribute("videoList", videoList);
        model.addAttribute("postList", postList);
        return "board/dualList";
    }

    @GetMapping("/search")
    public String searchAll(
            @RequestParam("keyword") String keyword,
            @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        // keyword는 네비바에서 입력 받으니까 비어있을 일은 거의 없지만,
        // 안전하게 hasText로 체크해도 됨.
        Page<Post> postPage = postService.getPage(pageable, keyword);
        Page<Video> videoPage = videoService.getPage(pageable, keyword);

        model.addAttribute("keyword", keyword);
        model.addAttribute("postPage", postPage);
        model.addAttribute("videoPage", videoPage);

        return "board/searchResult";
    }
    
}
