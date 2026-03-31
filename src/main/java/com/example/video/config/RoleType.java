package com.example.video.config;

import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(name = "role")
public enum RoleType {
    USER,      // 일반 회원
    MANAGER,   // 운영/매니저
    ADMIN,     // 최고 관리자
    BANNED;    // 정지된 계정


//    USER("ROLE_USER"),
//    ADMIN("ROLE_ADMIN");
//    RoleType(Strung roleName){
//    this.roleName = roleName;
//    }
//    private final String roleName;


}
