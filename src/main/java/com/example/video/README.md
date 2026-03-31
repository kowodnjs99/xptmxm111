# Community Board Project (학습용 커뮤니티 게시판)

Spring Boot로 만든 학습용 커뮤니티 게시판 프로젝트입니다.  
회원 가입 · 로그인 · 권한(ROLE) 관리 · 게시판(자유/영상) · 댓글/대댓글 · 검색 · 페이징까지 한 번에 경험해 보는 것을 목표로 했습니다.

> ⚠️ 이 프로젝트는 **운영용이 아닌, 개인 학습용**입니다.

---

## 1. 기술 스택

### Backend
- Java 17
- Spring Boot 3.x
- Spring Web (spring-boot-starter-web)
- Spring Data JPA (spring-boot-starter-data-jpa)
- Spring Security (spring-boot-starter-security)
- Hibernate

### Frontend
- Thymeleaf
- Thymeleaf Layout Dialect
- Thymeleaf Extras
    - `thymeleaf-extras-springsecurity6`
    - `thymeleaf-extras-java8time`
- Bootstrap 5
- Custom CSS (네비바/푸터 색상, 버튼 스타일 등)

### Database
- MariaDB
- JPA/Hibernate 자동 DDL (개발용)

### 기타
- Lombok
- Gradle (빌드 툴)

---

## 2. 주요 기능 정리

### 2-1. 회원 기능

- 회원 가입
    - `/member/chuga` (또는 `/member/join`)에서 회원 등록
    - 비밀번호는 **BCryptPasswordEncoder**로 암호화 저장
- 로그인 / 로그아웃
    - `/member/login` 로그인 페이지
    - 네비바에 로그인/로그아웃 버튼 노출
    - 로그인 시, 로그인한 사용자의 닉네임을 화면에 표시할 수 있음
- 권한(Role) 관리
    - `RoleType` enum: `ADMIN`, `MANAGER`, `USER`, `BANNED`
    - 관리자/매니저만 접근 가능한 **권한 변경 페이지** 구현
        - 회원 목록에서 체크박스로 대상 회원 선택
        - 선택한 회원의 권한을 일괄 변경 가능 (ADMIN은 보호)

### 2-2. 게시판 기능

#### 자유 게시판 (Post)

- 엔티티: `Post`
    - `subject` (제목)
    - `content` (내용)
    - `writer` (작성자 `Member` 연관관계)
    - `viewCount`
    - 생성일/수정일 (BaseEntity 또는 LocalDateTime 필드)
- 기능
    - 글 목록 보기 (페이징)
    - 글 상세 보기
    - 글 작성 (로그인한 사용자의 닉네임 = 작성자)
    - 글 수정 / 삭제
    - 제목 검색 + 검색 결과 페이징

#### 영상 게시판 (Video)

- 엔티티: `Video`
    - `title`
    - `content` (설명)
    - `youtubeUrl`
    - `writer` (작성자 `Member`)
    - `viewCount`
    - 생성일/수정일
- 기능
    - 영상 목록 보기 (페이징)
    - 영상 상세 보기
        - `youtubeUrl` 기반으로 YouTube iframe 출력
    - 영상 등록/수정/삭제
    - 제목 검색 + 검색 결과 페이징

### 2-3. 댓글 / 대댓글 기능

- 대상: 자유 게시판 / 영상 게시판
- 기능
    - 댓글 작성
    - 대댓글(답글) 구조
    - 최신순 정렬
    - 수정/삭제
    - 들여쓰기/정렬 등 UI 튜닝

### 2-4. 검색 기능

- 네비바 검색
    - 상단 네비바에서 전체 검색
    - 자유 게시판 + 영상 게시판 통합 검색 결과 페이지 제공
    - 검색 결과도 페이징 처리
- 개별 게시판 서브 검색바
    - 각 게시판(list 페이지)에 제목 검색용 서브 검색바
    - 검색 결과 + 페이징 유지

### 2-5. 레이아웃 / UI

- 공통 레이아웃: `templates/_layout/layout.html`
    - `inc_head`, `inc_navbar`, `inc_footer` 분리
    - Thymeleaf Layout Dialect로 각 페이지에서 `layout:decorate` 방식으로 재사용
- 네비바
    - 진한 초록색 배경 + 흰색 글자
    - 네비바 검색바
    - 로그인/로그아웃, 회원 메뉴
- 메인 페이지
    - 상단 검색바
    - 왼쪽: 자유 게시판 최신 글 목록 카드
    - 오른쪽: 영상 게시판 최신 글 목록 카드
- 푸터
    - 초록색 배경으로 화면 하단에 배치

---

## 3. 프로젝트 구조
```text
com.example.video
├─ Member
│  ├─ Member.java
│  ├─ MemberDTO.java
│  ├─ MemberRepository.java
│  ├─ MemberService.java
│  ├─ MemberDetails.java
│  └─ MemberController.java
├─ index
│  ├─ BoardController.java
│  └─ indexController.java
│
├─ Post
│  ├─ Post.java
│  ├─ PostDTO.java
│  ├─ PostRepository.java
│  ├─ PostService.java
│  └─ PostController.java
│
├─ Video
│  ├─ Video.java
│  ├─ VideoDTO.java
│  ├─ VideoRepository.java
│  ├─ VideoService.java
│  ├─ InvalidYoutubeUrlException.java
│  └─ VideoController.java
│
├─ VideoComment
│  ├─ VideoComment.java
│  ├─ VideoCommentRepository.java
│  ├─ VideoCommentService.java
│  └─ VideoCommentController.java
├─ PostComment
│  ├─ PostComment.java
│  ├─ PostCommentRepository.java
│  ├─ PostCommentService.java
│  └─ PostCommentController.java
│
├─security
├─ SecurityConfig.java
├─ UserDetailsServiceImpl.java
│
└─ config
   ├─ RoleType.java
   └─ MemberStatus.java

4. 실행 방법
4-1. 사전 준비

JDK 17 이상 설치

MariaDB 설치

Gradle 사용 가능 상태 (IntelliJ에서 Gradle 프로젝트로 열기)

4-2. 데이터베이스 생성
CREATE DATABASE board_project CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE USER 'boardUser'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON board_project.* TO 'boardUser'@'localhost';
FLUSH PRIVILEGES;


DB 이름, 계정, 비밀번호는 실제 사용 중인 값으로 변경하세요.

4-3. application.properties 예시
spring.application.name=boardProject

server.port=9006

spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/board_project
spring.datasource.username=boardUser
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect

# Thymeleaf 캐시 끄기 (개발용)
spring.thymeleaf.cache=false

4-4. 실행
# Gradle
./gradlew bootRun
# 또는 IntelliJ에서 Application 클래스 실행


브라우저에서:

메인 페이지: http://localhost:9006/

회원 목록: http://localhost:9006/member/list

자유 게시판: http://localhost:9006/post/list

영상 게시판: http://localhost:9006/video/list

5. 시큐리티 & 권한
5-1. 사용자 인증

UserDetailsServiceImpl에서 MemberRepository를 사용해 username 기준으로 회원 조회

MemberDetails가 UserDetails를 구현하고, 권한/비밀번호/계정 상태를 제공

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    String roleName = "ROLE_" + member.getRole().name();
    return List.of(new SimpleGrantedAuthority(roleName));
}

5-2. 권한 변경 기능

회원 목록 페이지

체크박스로 여러 회원 선택

"선택 회원 권한 변경" 버튼 클릭 → /member/roleUpdateForm POST

권한 변경 페이지

선택한 회원 목록과 현재 권한 표시

드롭다운으로 USER / MANAGER / BANNED 선택 후 변경 실행

백엔드 로직

MemberService.updateRole(List<Integer> memberIds, RoleType roleType)

memberRepository.findByIdIn(memberIds)

각 Member의 setRole(roleType) 호출

ADMIN은 보호 (변경 제외)

6. 개발 일정 요약 (학습 로그)

Day 1: 프로젝트 세팅, DB 연결, 기본 레이아웃

Day 2: 엔티티 설계 (Member, Post, Video, BaseEntity 등)

Day 3–4: 회원 가입, 로그인/로그아웃, Security 연동

Day 5–8: Post/Video 게시판 CRUD, 상세/수정/삭제, YouTube 연동

Day 9–11: 검색, 페이징, 댓글/대댓글, 정렬 튜닝, 스타일링

Day 12: 버그 수정 및 정리, README 작성

실제로는 계획보다 빠르게 진행했고, 권한 변경 페이지, 통합 검색, 메인 레이아웃, 대댓글 등 추가 기능까지 구현했습니다.



7. 학습 포인트

Spring Security로 직접 UserDetails, UserDetailsService 구현해보기

Enum(RoleType, MemberStatus)로 권한/상태 관리

연관관계 매핑 (Member ↔ Post/Video 작성자)

Pageable을 이용한 페이징 + 검색 결과 페이징

Thymeleaf Layout Dialect로 공통 레이아웃 관리

댓글/대댓글 구조 설계와 UI/JS 처리

실제 에러 로그를 보면서 디버깅 & 예외 페이지 통합





