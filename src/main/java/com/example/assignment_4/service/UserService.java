package com.example.assignment_4.service;

import com.example.assignment_4.dto.*;
import com.example.assignment_4.entity.User;
import com.example.assignment_4.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // ✅ 회원가입 + 이미지 동시 업로드
    public Long signupWithImage(SignupRequest req, MultipartFile file) throws IOException {
        if (!req.getPassword().equals(req.getPassword_check())) {
            throw new IllegalArgumentException("password_mismatch");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("duplicate_email");
        }

        if (userRepository.existsByNickname(req.getNickname())) {
            throw new IllegalArgumentException("duplicate_nickname");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword())) // 비밀번호 인코딩
                .nickname(req.getNickname())
                .build();

        // 🔹 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads";
            Files.createDirectories(Paths.get(uploadDir));

            // 확장자 추출
            String extension = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf("."));
            }

            // 짧은 랜덤 파일명 생성 (예: img_12a7f3.png)
            String shortName = "img_" + UUID.randomUUID().toString().substring(0, 6) + extension;
            Path path = Paths.get(uploadDir, shortName);
            Files.write(path, file.getBytes());

            // DB에는 URL만 저장
            String imageUrl = "http://localhost:8080/uploads/" + shortName;
            user.setProfileImage(imageUrl);
        }

        userRepository.save(user);
        return user.getId();
    }

    // 로그인
//    public LoginResponse login(String email, String password) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("invalid_credentials"));
//
//        // 삭제된 계정 로그인 차단
//        if (user.getDeletedAt() != null) {
//            throw new RuntimeException("deleted_user");
//        }
//
//        if (!user.getPassword().equals(password)) {
//            throw new RuntimeException("emailOrPassword_mismatch");
//        }
//
//        // (JWT 발급 로직은 생략)
//        return new LoginResponse(
//                new UserInfo(user.getId(), user.getEmail(), user.getNickname(),user.getProfileImage()),
//                "eyJhbGciOi..." // 토큰 예시
//        );
//    }



    // =============================
    // 닉네임 단독 수정
    // =============================
    public void updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));

        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("nickname_empty");
        }

        // 중복 체크 (본인 제외)
        if (!nickname.equals(user.getNickname()) &&
                userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("duplicate_nickname");
        }

        user.setNickname(nickname);
        userRepository.save(user);
    }

    // =============================
    // 이미지 단독 수정
    // =============================
    public String updateProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file_empty");
        }

        String uploadDir = "uploads";
        Files.createDirectories(Paths.get(uploadDir));

        // 기존 이미지 삭제
        if (user.getProfileImage() != null) {
            String oldImagePath = user.getProfileImage(); // "/uploads/img_123.png"
            try {
                if (oldImagePath.startsWith("/uploads/")) {
                    Path oldFile = Paths.get("." + oldImagePath);
                    Files.deleteIfExists(oldFile);
                }
            } catch (IOException ignored) {}
        }

        String extension = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf("."));
        }

        String shortName = "img_" + UUID.randomUUID().toString().substring(0, 6) + extension;
        Path path = Paths.get(uploadDir, shortName);
        Files.write(path, file.getBytes());

        String imageUrl = "/uploads/" + shortName;

        user.setProfileImage(imageUrl);
        userRepository.save(user);

        return "http://localhost:8080" + imageUrl;
    }

//    // 닉네임 수정
//    public void updateProfile(Long userId, ProfileUpdateRequest req) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("user_not_found"));
//
//        // 닉네임 중복 체크 추가
//        if (userRepository.existsByNickname(req.getNickname())) {
//            throw new IllegalArgumentException("duplicate_nickname");
//        }
//
//        user.setNickname(req.getNickname());
//        userRepository.save(user);
//    }
// =============================
// 닉네임 + 이미지 동시 수정
// =============================
    public void updateNicknameAndImage(Long userId, String nickname, MultipartFile file) throws Exception {

        if (nickname != null && !nickname.isBlank()) {
            updateNickname(userId, nickname); // 재사용
        }

        if (file != null && !file.isEmpty()) {
            updateProfileImage(userId, file); // 재사용
        }
    }

     public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new RuntimeException("user_not_found"));

         if (file == null || file.isEmpty()) {
             throw new IllegalArgumentException("file_empty");
         }

         String uploadDir = "uploads";
         Files.createDirectories(Paths.get(uploadDir));

         // 🔹 기존 이미지 삭제(선택)
         // DB에 이전 이미지가 있을 경우 실제 파일 삭제
         if (user.getProfileImage() != null) {
             String oldImagePath = user.getProfileImage(); // "/uploads/img_123abc.png"
             try {
                 if (oldImagePath.startsWith("/uploads/")) {
                     Path oldFile = Paths.get("." + oldImagePath); // "./uploads/img_123abc.png"
                     Files.deleteIfExists(oldFile);
                 }
             } catch (IOException ignored) {
                 // 삭제 실패해도 기능은 지속
             }
         }

         // 🔹 확장자 추출
         String extension = "";
         String original = file.getOriginalFilename();
         if (original != null && original.contains(".")) {
             extension = original.substring(original.lastIndexOf("."));
         }

         // 🔹 랜덤 파일명 생성 (짧고 안전하게)
         String shortName = "img_" + UUID.randomUUID().toString().substring(0, 6) + extension;

         Path path = Paths.get(uploadDir, shortName);
         Files.write(path, file.getBytes());

         // ✅ DB에는 상대 경로만 저장
         String imageUrl = "/uploads/" + shortName;

         user.setProfileImage(imageUrl); // ✅ 상대 경로 저장
         userRepository.save(user);

         // ✅ 프런트에는 전체 URL 반환
         return "http://localhost:8080" + imageUrl;
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
        return new UserInfo(user.getId(), user.getEmail(), user.getNickname(), user.getProfileImage());
    }

    // 회원 탈퇴
    // 회원 탈퇴 (Soft Delete)
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));

        // 실제 삭제 아니고  삭제 시간만 기록
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // 비밀번호 변경
    public void updatePassword(Long userId, String newPassword, String newPassword_check) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user_not_found"));

        // ✅ 2새 비밀번호 일치 확인
        if (!newPassword.equals(newPassword_check)) {
            throw new IllegalArgumentException("password_mismatch");
        }

        // 🔥 기존 비밀번호와 동일한지 체크
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("same_password");
        }

        user.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 암호화 적용
        userRepository.save(user);
    }
}
