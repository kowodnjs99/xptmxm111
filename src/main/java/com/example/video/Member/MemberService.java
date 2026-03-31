package com.example.video.Member;

import com.example.video.config.MemberStatus;
import com.example.video.config.RoleType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
//    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Member> getList() {
        return memberRepository.findAll();
    }


    /**
     * 체크박스로 선택된 회원들 전부 조회
     */
    @Transactional(readOnly = true)
    public List<Member> findAllByIds(List<Integer> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return List.of();
        }
        // 리포지토리에서 한 번에 가져오기
        return memberRepository.findByIdIn(memberIds);
        // 또는 return memberRepository.findAllById(memberIds);
    }

    /**
     * 선택된 회원들의 role 을 일괄 변경
     */
    @Transactional
    public void updateRole(List<Integer> memberIds, RoleType roleType) {

        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("권한을 변경할 회원이 없습니다.");
        }

        // ADMIN 으로 바꾸는 건 막고 싶으면
        if (roleType == RoleType.ADMIN) {
            throw new IllegalArgumentException("ADMIN 권한은 여기서 변경할 수 없습니다.");
        }

        List<Member> members = memberRepository.findByIdIn(memberIds);
        if (members.isEmpty()) {
            throw new IllegalArgumentException("선택한 회원 정보를 찾을 수 없습니다.");
        }

        for (Member member : members) {
            // 이미 ADMIN 인 계정은 건너뛰기 (원하면 예외 던져도 됨)
            if (member.getRole() == RoleType.ADMIN) {
                log.warn("[updateRole] ADMIN 계정은 변경 불가. id={}, username={}",
                        member.getId(), member.getUsername());
                continue;
            }

            member.setRole(roleType);   // 🔹 실제 role 변경
        }

        // @Transactional 이라 변경만 해도 flush 되지만,
        // 헷갈리면 명시적으로 saveAll 해도 됨
        memberRepository.saveAll(members);
    }





    public void join(MemberDTO dto) {
        Member member = new Member();
        member.setUsername(dto.getUsername());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setNickname(dto.getNickname());
        member.setEmail(dto.getEmail());
        member.setRole(RoleType.USER);

        memberRepository.save(member);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

    public List<Member> list(){
        return memberRepository.findAll();
    }

    // ✅ 페이징용
    public Page<Member> getPage(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }


    public Member view(MemberDTO paramDTO) {
        Optional<Member> optionalMember = memberRepository.findById(paramDTO.getId());
        Member member = null;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();
        }
        return member;
    }
    public void chugaProc(MemberDTO memberDTO) {
        Member member = new Member();
        member.setUsername(memberDTO.getUsername());
//        member.setPassword(memberDTO.getPassword());
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        member.setEmail(memberDTO.getEmail());
        member.setNickname(memberDTO.getNickname());
        member.setRole(RoleType.USER);
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);
    }

    @Transactional
    public void sujungProc(MemberDTO memberDTO){
        Member member = memberRepository.findById(memberDTO.getId())
                .orElseThrow(EntityNotFoundException::new);
        member.update(memberDTO);
    }

    @Transactional
    public void sakjeProc(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getId())
                .orElseThrow(EntityNotFoundException::new);

        memberRepository.delete(member);
    }


//        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));

}
