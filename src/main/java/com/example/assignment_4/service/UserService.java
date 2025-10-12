package com.example.assignment_4.service;

import com.example.assignment_4.dto.LoginResponse;
import com.example.assignment_4.dto.ProfileUpdateRequest;
import com.example.assignment_4.dto.SignupRequest;
import com.example.assignment_4.dto.UserInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {

    // 간단하게 메모리에 회원 정보를 저장 (DB 대체)
    private Map<String, Object> userData = new HashMap<>();
    private boolean deleted = false; // 탈퇴 여부 flag

    public UserService() {
        // 초기 회원 정보 세팅 (이미 회원가입된 상태라고 가정)
        userData.put("id", 1L);
        userData.put("email", "example@example.com");
        userData.put("nickname", "joody");
    }

    /**
     * 이메일 & 비밀번호 검증 로직
     */
    public boolean validateCredentials(String email, String password) {
        if (deleted) return false;
        return Objects.equals(userData.get("email"), email) &&
                Objects.equals(userData.get("password"), password);

        /* Objects.equals(a, b)는 a나 b가 null이어도 절대 NPE를 안 내고 안전하게 false를 반환함. */
    }

    /**
     * 로그인 처리 로직
     */
    public LoginResponse login(String email, String password) {
        if (!validateCredentials(email, password)) {
            throw new RuntimeException("invalid_credentials");
        }

        return new LoginResponse(
                new UserInfo(1L, (String) userData.get("nickname")),
                "eyJhbGciOi..." // 예시 토큰
        );
    }

    /* 회원가입 */
    public Long signup(SignupRequest req) {
        if (!req.getPassword().equals(req.getPassword_check())) {
            throw new IllegalArgumentException("invalid_request");
        }

        // 중복 이메일/닉네임 체크 (테스트용)
        if ("example@example.com".equals(req.getEmail()) || "joody".equals(req.getNickname())) {
            throw new IllegalStateException("duplicate_email_or_nickname");
        }

        // 신규 데이터 저장
        userData.put("id", 2L);
        userData.put("email", req.getEmail());
        userData.put("nickname", req.getNickname());
        userData.put("password", req.getPassword());
        deleted = false; // 새 회원은 탈퇴 상태 아님

        return 2L;
    }

    /* 회원정보 수정 */
    public void updateProfile(ProfileUpdateRequest req) {
        if (deleted) {
            throw new RuntimeException("user_not_found");
        }

        if ("joody".equals(req.getNickname())) {
            throw new IllegalStateException("duplicate_nickname");
        }

        // 닉네임 변경
        userData.put("nickname", req.getNickname());
    }

    /* 회원탈퇴 */
    public void withdrawUser() {
        // 실제 환경에서는 DB에서 delete or 탈퇴 플래그 변경
        deleted = true;
    }

    /* 회원정보 조회 */
    public Map<String, Object> getUserProfile() {
        if (deleted) {
            return null;
        }
        return userData;
    }

    /*프로필 이미지 업로드*/

    public void updateProfileImage(String imageUrl) {
        if (deleted) {
            throw new RuntimeException("user_not_found");
        }
        userData.put("profile_image", imageUrl);
    }

    /*프로필 이미지 삭제*/
    public void deleteProfileImage() {
        if (deleted) {
            throw new RuntimeException("user_not_found");
        }

        String imageUrl = (String) userData.get("profile_image");
        if (imageUrl != null) {
            // 파일 경로 추출
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get("uploads", filename);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("file_delete_failed");
            }

            userData.remove("profile_image");
        }
    }



    public void updatePassword(String newPassword) {
        if (deleted) {
            throw new IllegalArgumentException("user_not_found");
        }

        // 실제로는 비밀번호 암호화 후 DB 업데이트해야 함
        userData.put("password", newPassword);
    }


}
