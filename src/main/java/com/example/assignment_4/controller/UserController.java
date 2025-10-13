package com.example.assignment_4.controller;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.LoginRequest;
import com.example.assignment_4.dto.LoginResponse;
import com.example.assignment_4.dto.ProfileUpdateRequest;
import com.example.assignment_4.dto.SignupRequest;
import com.example.assignment_4.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 로그인 검증 */
    @PostMapping("/login/validate")
    public ResponseEntity<ApiResponse<Void>> validateLogin(
            @Valid @RequestBody LoginRequest req
    ) {
        boolean valid = userService.validateCredentials(req.getEmail(), req.getPassword());
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("invalid_credentials", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("validation_success", null));
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req
    ) {
        var response = userService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new ApiResponse<>("login_success", response));
    }

    /** 회원가입 */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(
            @Valid @RequestBody SignupRequest req
    ) {
        Long userId = userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("register_success", userId));
    }

    /** 닉네임 수정 */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        userService.updateProfile(req);
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }

    /** 프로필 이미지 업로드 */
    @PostMapping("/profile/image")
    public ResponseEntity<ApiResponse<?>> uploadProfileImage(
            @RequestPart("profile_image") MultipartFile file
    ) throws Exception {
        String imageUrl = userService.uploadProfileImage(file);
        return ResponseEntity.ok(new ApiResponse<>("profile_image_uploaded", imageUrl));
    }

    /** 프로필 이미지 삭제 */
    @DeleteMapping("/profile/image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage() {
        userService.deleteProfileImage();
        return ResponseEntity.ok(new ApiResponse<>("profile_image_deleted", null));
    }

    /** 회원정보 조회 */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getProfile() {
        var profile = userService.getUserProfile();
        return ResponseEntity.ok(new ApiResponse<>("read_success", profile));
    }

    /** 회원 탈퇴 */
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> withdraw() {
        userService.withdrawUser();
        return ResponseEntity.ok(new ApiResponse<>("withdraw_success", null));
    }

    /** 비밀번호 수정 */
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody Map<String, String> body
    ) {
        String newPassword = body.get("password");
        userService.updatePassword(newPassword);
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }
}
