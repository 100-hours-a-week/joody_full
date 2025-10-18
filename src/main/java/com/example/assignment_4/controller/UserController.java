package com.example.assignment_4.controller;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.LoginRequest;
import com.example.assignment_4.dto.LoginResponse;
import com.example.assignment_4.dto.ProfileUpdateRequest;
import com.example.assignment_4.dto.SignupRequest;
import com.example.assignment_4.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User API", description = "회원가입, 로그인, 프로필 관리, 비밀번호 변경 등 사용자 관련 기능 제공")
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인 유효성 검증", description = "이메일과 비밀번호 조합이 유효한지 검증합니다. 실패 시 401을 반환합니다.")
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

    @Operation(summary = "로그인", description = "회원 로그인 처리 후 JWT 토큰과 사용자 정보를 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req
    ) {
        var response = userService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new ApiResponse<>("login_success", response));
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다. 이메일, 비밀번호, 닉네임을 입력받습니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(
            @Valid @RequestBody SignupRequest req
    ) {
        Long userId = userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("register_success", userId));
    }

    @Operation(summary = "닉네임 수정", description = "로그인된 사용자의 닉네임을 변경합니다.")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        userService.updateProfile(req);
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 업로드하고 서버에 저장된 이미지 URL을 반환합니다.")
    @PostMapping("/profile/image")
    public ResponseEntity<ApiResponse<?>> uploadProfileImage(
            @RequestPart("profile_image") MultipartFile file
    ) throws Exception {
        String imageUrl = userService.uploadProfileImage(file);
        return ResponseEntity.ok(new ApiResponse<>("profile_image_uploaded", imageUrl));
    }

    @Operation(summary = "프로필 이미지 삭제", description = "등록된 프로필 이미지를 삭제합니다.")
    @DeleteMapping("/profile/image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage() {
        userService.deleteProfileImage();
        return ResponseEntity.ok(new ApiResponse<>("profile_image_deleted", null));
    }

    @Operation(summary = "회원 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getProfile() {
        var profile = userService.getUserProfile();
        return ResponseEntity.ok(new ApiResponse<>("read_success", profile));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자를 탈퇴 처리합니다.")
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> withdraw() {
        userService.withdrawUser();
        return ResponseEntity.ok(new ApiResponse<>("withdraw_success", null));
    }

    @Operation(summary = "비밀번호 수정", description = "현재 로그인된 사용자의 비밀번호를 변경합니다.")
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody Map<String, String> body
    ) {
        String newPassword = body.get("password");
        userService.updatePassword(newPassword);
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }
}
