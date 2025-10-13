package com.example.assignment_4.service;

import com.example.assignment_4.dto.LoginResponse;
import com.example.assignment_4.dto.ProfileUpdateRequest;
import com.example.assignment_4.dto.SignupRequest;
import com.example.assignment_4.dto.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class UserService {

    private SignupRequest user; // 한 명의 유저만 관리
    private boolean loggedIn = false; // 로그인 여부

    public UserService() {
        // 테스트용 초기 유저
        user = new SignupRequest();
        user.setEmail("example@example.com");
        user.setPassword("P@ssw0rd!");
        user.setPassword_check("P@ssw0rd!");
        user.setNickname("joody");
    }

    // 로그인 검증
    public boolean validateCredentials(String email, String password) {
        return user != null &&
                Objects.equals(user.getEmail(), email) &&
                Objects.equals(user.getPassword(), password);
    }

    // 로그인 처리
    public LoginResponse login(String email, String password) {
        if (!validateCredentials(email, password)) {
            throw new RuntimeException("invalid_credentials");
        }
        loggedIn = true;
        return new LoginResponse(
                new UserInfo(1L, user.getNickname()),
                "eyJhbGciOi..." // 토큰 예시
        );
    }

    // 회원가입
    public Long signup(SignupRequest req) {
        if (!req.getPassword().equals(req.getPassword_check())) {
            throw new IllegalArgumentException("password_mismatch");
        }

        // 기존 유저 덮어쓰기
        user = req;
        return 1L;
    }

    // 닉네임 수정
    public void updateProfile(ProfileUpdateRequest req) {
        if (!loggedIn || user == null) {
            throw new RuntimeException("not_logged_in");
        }
        user.setNickname(req.getNickname());
    }

    // 프로필 이미지 업로드
    public String uploadProfileImage(MultipartFile file) throws IOException {
        if (user == null) throw new RuntimeException("user_not_found");

        String uploadDir = "uploads";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, filename);
        Files.write(path, file.getBytes());

        String imageUrl = "http://localhost:8080/uploads/" + filename;
        user.setProfile_image(imageUrl);
        return imageUrl;
    }

    // 프로필 이미지 삭제
    public void deleteProfileImage() {
        if (user == null) throw new RuntimeException("user_not_found");
        user.setProfile_image(null);
    }

    // 회원정보 조회
    public SignupRequest getUserProfile() {
        if (!loggedIn) throw new RuntimeException("not_logged_in");
        return user;
    }

    // 회원 탈퇴
    public void withdrawUser() {
        user = null;
        loggedIn = false;
    }

    // 비밀번호 변경
    public void updatePassword(String newPassword) {
        if (!loggedIn) throw new RuntimeException("not_logged_in");
        user.setPassword(newPassword);
        user.setPassword_check(newPassword);
    }
}
