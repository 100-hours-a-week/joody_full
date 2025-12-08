## 🚀 Backend — Spring Boot REST API & JWT Security
### 📌 프로젝트 개요

**아무말 대잔치(Backend)**
사용자 인증부터 게시글/댓글/프로필 관리까지 제공하는 커뮤니티 서비스 백엔드입니다.

단순 CRUD가 아닌:
- JWT 기반 인증·인가
- Refresh Token 쿠키 보안
- 게시글/댓글/좋아요/조회수
- 프로필 이미지 업로드
- RESTful API 아키텍처
까지 직접 설계하고 구현했습니다.
<br />
<br />

## 📦 Frontend Repositories

> 아무말 대잔치 프론트엔드는 Vanilla JS 기반 SPA 버전과, React 기반 리팩토링 버전으로 구성되어 있습니다.

<div align="center">

| 프로젝트 버전 | Repository | 설명 |
|--------------|------------|-------|
| **Vanilla JavaScript (SPA)** | **https://github.com/100-hours-a-week/joody_front** | VDOM 기반 SPA 엔진 직접 구현, 인피니티 스크롤, 커스텀 store 상태관리 |
| **React (Refactoring)** | **https://github.com/100-hours-a-week/joody_React** | React SPA, Styled-Components, JWT 인증, 무한스크롤, 커스텀 hooks |

</div>

<br/>

## 🛠 기술 스택
### Backend
- Spring Boot 3.x
- Spring Security
- JPA / Hibernate
- MySQL
- JWT (io.jsonwebtoken)
- Lombok
<br />

### Tools
- Swagger UI
- Postman
- Git / GitHub
<br />

### 🔥 1. 전체 아키텍처
```plaintext
Client (HTML/JS)
     ↓  fetch API
REST API (Spring Boot)
     ↓
JPA/Hibernate
     ↓
MySQL
```
<br />

### 🔐 2. Spring Security + JWT 인증/인가
**✔ Access Token + Refresh Token 구조**
| 구분                | 전달 방식                     | 설명             |
| ----------------- | ------------------------- | -------------- |
| **Access Token**  | Authorization 헤더 (Bearer) | API 요청 시 인증    |
| **Refresh Token** | HttpOnly Cookie           | XSS 방어, 자동 재발급 |

✔ 주요 보안 구성 요소

- CustomUserDetails — UserDetails 구현

- CustomUserDetailsService — email/userId 기반 조회

- JwtTokenProvider — JWT 생성·검증·파싱

- JwtAuthenticationFilter — 요청마다 JWT 인증 처리

- SecurityConfig — 필터 체인, 인증/인가 규칙, CORS 설정
<br />

**✔ 인증 흐름 요약**

- 로그인
 - AuthenticationManager로 이메일/비번 인증
 - AccessToken + RefreshToken 발급 (쿠키 저장)

- 요청마다 JwtAuthenticationFilter 작동
 - Authorization 헤더의 토큰 검증
 - SecurityContext에 인증 저장

- AccessToken 만료
 - /auth/refresh로 RefreshToken 사용해 재발급

- 로그아웃
 - RefreshToken 쿠키 삭제
<br />
<br />


### 🧩 3. REST API 주요 기능

Security뿐 아니라, 실제 커뮤니티 서비스 기능 전체를 구현했습니다.

**🧑‍💻 User API**
| 기능          | 설명                     |
| ----------- | ---------------------- |
| 회원가입        | 이메일 중복 체크, 비밀번호 암호화 저장 |
| 로그인         | JWT 발급 및 쿠키 저장         |
| 프로필 조회      | userId 기반 조회           |
| 프로필 수정      | 닉네임/이메일 변경             |
| 비밀번호 변경     | 기존 비밀번호 검증 후 변경        |
| 프로필 이미지 업로드 | 이미지 저장 후 URL 반환        |

**📝 Post API**
| 기능        | 설명                |
| --------- | ----------------- |
| 게시글 작성    | 텍스트 + 이미지 업로드     |
| 게시글 단건 조회 | 조회 시 viewCount 증가 |
| 게시글 수정    | 자신의 글만 수정         |
| 게시글 삭제    | soft delete       |
| 게시글 목록 조회 | 최신순 페이징 / 무한스크롤   |
| 좋아요       | 좋아요 증가/취소         |
| 조회수       | 조회 시 자동 증가        |


**💬 Comment API**
| 기능       | 설명          |
| -------- | ----------- |
| 댓글 작성    | 로그인 유저만 가능  |
| 댓글 수정    | 본인 댓글만 수정   |
| 댓글 삭제    | soft delete |
| 댓글 목록 조회 | 게시글 기반 목록   |


**❤️ Like API**
중복 좋아요 방지 및 관리 위한 Likes 엔티티 사용

| 기능       | 설명                  |
| -------- | ------------------- |
| 좋아요      | 게시글에 좋아요 등록         |
| 좋아요 취소   | 좋아요 상태 제거           |
| 좋아요 수 갱신 | Post.likeCount 업데이트 |

<br/>
<br/>


### 📚 4. 백엔드 패키지 구조
```plaintext
src/main/java/com/example/assignment_4
├── auth/                       # 로그인/로그아웃/토큰 재발급
├── security/                   # Spring Security (JWT 인증/인가)
│   ├── JwtTokenProvider
│   ├── JwtAuthenticationFilter
│   ├── SecurityConfig
│   ├── CustomUserDetails
│   └── CustomUserDetailsService
│
├── controller/                 # REST API 컨트롤러
│   ├── UserController
│   ├── PostController
│   └── CommentController
│
├── service/                    # 비즈니스 로직
│   ├── UserService
│   ├── PostService
│   └── CommentService
│
├── repository/                 # 데이터 접근
│   ├── UserRepository
│   ├── PostRepository
│   └── CommentRepository
│
├── entity/                     # JPA 엔티티
│   ├── User
│   ├── Post
│   ├── Comment
│   └── Likes
│
└── common/
    ├── ApiResponse
    └── ExceptionHandler
```

### 📊 5. 모델링 (E-R Diagram)
`E-R Diagram`  
요구사항을 기반으로 모델링한 E-R Diagram입니다.  
<br/>

<p align="center">
  <img width="1207" height="569" alt="Image" src="https://github.com/user-attachments/assets/e921137e-8bbd-468a-b160-76d6be735cde" />
</p>
