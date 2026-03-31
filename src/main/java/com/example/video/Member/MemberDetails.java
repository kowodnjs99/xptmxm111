package com.example.video.Member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Member 엔티티를 Spring Security 쪽에서 사용할 수 있게
 * 감싸주는 래퍼 클래스.
 */
public class MemberDetails implements UserDetails {

    private final Member member;

    public MemberDetails(Member member) {
        this.member = member;
    }

    // 필요하면 컨트롤러/서비스에서 Member 꺼내쓰려고 제공
    public Member getMember() {
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예) RoleType 이라는 enum 필드가 Member 에 있다고 가정
        // ROLE_USER, ROLE_ADMIN 이런 형태로 변환
        String roleName = "ROLE_" + member.getRole().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;    // 계정 만료 기능 안 쓰면 그냥 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;    // 잠금 기능 안 쓰면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    // 비밀번호 만료 기능 안 쓰면 true
    }

    @Override
    public boolean isEnabled() {
        return true;    // 탈퇴/정지 상태 체크 넣고 싶으면 여기서 처리
    }
}
