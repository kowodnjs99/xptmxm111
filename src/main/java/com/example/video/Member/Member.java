package com.example.video.Member;


import com.example.video.config.MemberStatus;
import com.example.video.config.RoleType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "members")
public class Member  {
//extends BaseEntity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false,length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RoleType role; // USER, ADMIN 등

    @Column(nullable = false, unique = true,length = 255)
    private String email; // 선택이지만 권장

    @Column(nullable = false,length=50)
    private String nickname; // 화면용 표시 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status; // ACTIVE / INACTIVE 멤버 상태


    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;


    // Member 엔티티
    public void update(MemberDTO dto) {
        this.email = dto.getEmail();
        this.nickname = dto.getNickname();
    }



}
