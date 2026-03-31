package com.example.video.Video.like;


import com.example.video.Member.Member;
import com.example.video.Member.MemberDetails;
import com.example.video.Member.MemberService;
import com.example.video.Video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoLikeController {

    private final VideoLikeService videoLikeService;

    @PostMapping("/{id}/like")
    public Map<String, Object> toggleLike(
            @PathVariable("id") int id,
            @AuthenticationPrincipal MemberDetails userDetails
    ) {
        if (userDetails == null) {
            throw new IllegalStateException("로그인 필요");
        }
        Member member = userDetails.getMember();
        boolean liked = videoLikeService.toggle(id, member);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);

        return result;
    }
}
