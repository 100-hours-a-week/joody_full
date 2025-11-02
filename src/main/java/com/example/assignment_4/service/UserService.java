package com.example.assignment_4.service;

import com.example.assignment_4.dto.*;
import com.example.assignment_4.entity.User;
import com.example.assignment_4.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // 회원가입
    public Long signup(SignupRequest req) {
        if (!req.getPassword().equals(req.getPassword_check())) {
            throw new IllegalArgumentException("password_mismatch");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("duplicate_email");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .nickname(req.getNickname())
                .profileImage(req.getProfile_image())
                .build();

        userRepository.save(user);
        return user.getId();
    }

    // 로그인
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("invalid_credentials"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("invalid_credentials");
        }

        // (JWT 발급 로직은 생략)
        return new LoginResponse(
                new UserInfo(user.getId(), user.getNickname()),
                "eyJhbGciOi..." // 토큰 예시
        );
    }

    // 닉네임 수정
    public void updateProfile(Long userId, ProfileUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));

        user.setNickname(req.getNickname());
        userRepository.save(user);
    }

     //프로필 이미지 업로드
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));

        String uploadDir = "uploads";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, filename);
        Files.write(path, file.getBytes());

        String imageUrl = "http://localhost:8080/uploads/" + filename;
        user.setProfileImage(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    // 프로필 이미지 삭제
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));
        user.setProfileImage(null);
        userRepository.save(user);
    }

    // 회원정보 조회
    public UserInfo getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));
        return new UserInfo(user.getId(), user.getNickname());
    }

    // 회원 탈퇴
    public void withdrawUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 비밀번호 변경
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
