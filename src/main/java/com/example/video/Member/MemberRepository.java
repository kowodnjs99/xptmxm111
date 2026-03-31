package com.example.video.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUsername(String username);
    // 🔹 권한 변경 대상 회원들 한 번에 조회
    List<Member> findByIdIn(List<Integer> ids);
    // 또는 JpaRepository 기본 메서드 findAllById(...) 써도 되지만,
    // 이렇게 해두면 코드가 더 읽기 쉬움

    String findBynickname(String nickname);
}

