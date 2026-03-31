package com.example.video.Member;

import com.example.video.Video.InvalidYoutubeUrlException;
import com.example.video.config.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@RequestMapping("/member")
@Controller
public class MemberController {

    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model

    ) {
        Page<Member> page = memberService.getPage(pageable);

        model.addAttribute("memberPage", page);         // 페이지 정보
        model.addAttribute("MemberList", page.getContent()); // 실제 목록

        int current = page.getNumber() + 1;                 // 현재 페이지(1부터)
        int start = Math.max(1, current - 4);
        int end = Math.min(page.getTotalPages(), current + 4);

        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);

        return "member/list";
    }


    @GetMapping("/view/{id}")
    public String view(
            Model model,
            @PathVariable("id") Integer id
    ) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(id);
        Member member = memberService.view(memberDTO);
        if (member == null) {
            model.addAttribute("errorCode", "err0001");
            model.addAttribute("errorMsg", "존재하지 않는 회원입니다.");
            return "error/error";
        }
        model.addAttribute("member", member);
        return "member/view";
    }

    @GetMapping("/chuga")
    public String chuga() {
        return "member/chuga";
    }

    @PostMapping("/chugaProc")
    public String chugaProc(
            MemberDTO memberDTO
    ) {
        memberService.chugaProc(memberDTO);
        return "redirect:/member/list";
    }


    @GetMapping("/sujung/{id}")
    public String sujung(
            Model model,
            @PathVariable("id") Integer id
    ) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(id);
        Member member = memberService.view(memberDTO);
        if (member == null) {
            model.addAttribute("errorCode", "err0001");
            model.addAttribute("errorMsg", "존재하지 않는 회원입니다.");
            return "error/error";
        }
        model.addAttribute("member", member);
        return "member/sujung";
    }

    @PostMapping("/sujungProc")
    public String sujungProc(
            Model model,
            MemberDTO memberDTO
    ) {
        System.out.println("ID = " + memberDTO.getId());
        memberService.sujungProc(memberDTO);
        return "redirect:/board/dualList";
    }


    @GetMapping("/sakje/{id}")
    public String sakje(
            Model model,
            @PathVariable("id") Integer id
    ) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(id);
        Member member = memberService.view(memberDTO);
        if (member == null) {
            model.addAttribute("errorCode", "err0001");
            model.addAttribute("errorMsg", "존재하지 않는 회원입니다.");
            return "error/error";
        }
        model.addAttribute("member", member);
        return "member/sakje";
    }


    @PostMapping("/sakjeProc")
    public String sakjeProc(
            Model model,
            MemberDTO memberDTO
    ) {
        System.out.println("ID = " + memberDTO.getId());
        memberService.sakjeProc(memberDTO);
        return "redirect:/board/dualList";
    }

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "member/join";
    }

    @PostMapping("/join")
    public String joinProc(@ModelAttribute MemberDTO memberDTO) {
        memberService.join(memberDTO);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";  // templates/member/login.html
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/roleUpdateForm")
    public String roleUpdateForm(
            @RequestParam(value = "memberIds", required = false) List<Integer> memberIds,
            Model model,
            RedirectAttributes rttr
    ) {
        if (memberIds == null || memberIds.isEmpty()) {
            rttr.addFlashAttribute("errorMsg", "권한을 변경할 회원을 한 명 이상 선택해 주세요.");
            return "redirect:/member/list";
        }

        List<Member> members = memberService.findAllByIds(memberIds);
        model.addAttribute("members", members);

        return "member/roleUpdate";   // templates/member/roleUpdate.html
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/roleUpdate")
    public String roleUpdate(
            @RequestParam(value = "memberIds", required = false) List<Integer> memberIds,
            @RequestParam("roleType") RoleType roleType,
            RedirectAttributes rttr
    ) {
        if (memberIds == null || memberIds.isEmpty()) {
            rttr.addFlashAttribute("errorMsg", "권한을 변경할 회원이 선택되지 않았습니다.");
            return "redirect:/member/list";
        }

        try {
            memberService.updateRole(memberIds, roleType);
            rttr.addFlashAttribute("msg", "선택한 회원들의 권한이 변경되었습니다.");
        } catch (Exception e) {
            rttr.addFlashAttribute("errorMsg", "권한 변경 중 오류가 발생했습니다.");
        }

        return "redirect:/member/list";
    }


}