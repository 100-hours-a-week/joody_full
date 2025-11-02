package com.example.assignment_4.controller;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.*;
import com.example.assignment_4.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<ApiResponse<Long>> signup(@Valid @RequestBody SignupRequest req) {
        Long userId = userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("register_success", userId));
    }


    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse response = userService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new ApiResponse<>("login_success", response));
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "닉네임 수정", description = "회원 닉네임을 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        userService.updateProfile(userId, req);
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }


    @PostMapping("/{userId}/profile/image")
    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 업로드하고 서버에 저장된 이미지 URL을 반환합니다.")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(
            @PathVariable Long userId,
            @RequestPart("profile_image") MultipartFile file
    ) throws Exception {
        String imageUrl = userService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(new ApiResponse<>("profile_image_uploaded", imageUrl));
    }



    @DeleteMapping("/{userId}/profile/image")
    @Operation(summary = "프로필 이미지 삭제", description = "등록된 프로필 이미지를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(@PathVariable Long userId) {
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok(new ApiResponse<>("profile_image_deleted", null));
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "회원 정보 조회", description = "사용자 프로필 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserInfo>> getProfile(@PathVariable Long userId) {
        UserInfo profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(new ApiResponse<>("read_success", profile));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "사용자를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> withdraw(@PathVariable Long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("withdraw_success", null));
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordRequest request
    ) {
        userService.updatePassword(userId, request.getPassword());
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }
}
