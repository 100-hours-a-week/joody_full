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

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임, 프로필 이미지를 함께 전송하여 회원가입합니다.")
    public ResponseEntity<ApiResponse<Long>> signup(
            @Valid @RequestPart("user") SignupRequest req,
            @RequestPart(value = "profile_image", required = false) MultipartFile file
    ) throws Exception {

        Long userId = userService.signupWithImage(req, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("register_success", userId));
    }




    // ======================
// 🎨 프로필 수정 리팩토링
// ======================
    @PutMapping("/{userId}/profile")
    @Operation(summary = "닉네임 + 프로필 이미지 수정", description = "닉네임과 이미지 모두를 multipart/form-data로 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @PathVariable Long userId,
            @RequestPart(value = "nickname", required = false) String nickname,
            @RequestPart(value = "profile_image", required = false) MultipartFile file
    ) throws Exception {

        userService.updateNicknameAndImage(userId, nickname, file);
        return ResponseEntity.ok(new ApiResponse<>("update_success", null));
    }

    // ======================
// ✨ 닉네임만 변경(JSON)
// ======================
    @PutMapping("/{userId}/profile/nickname")
    @Operation(summary = "닉네임만 수정", description = "JSON 형태로 닉네임을 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request
    ) throws Exception {

        String nickname = request.get("nickname");
        userService.updateNickname(userId, nickname);
        return ResponseEntity.ok(new ApiResponse<>("nickname_update_success", null));
    }

    // ======================
// 🖼 이미지 단독 변경(multipart)
// ======================
    @PutMapping("/{userId}/profile/image")
    @Operation(summary = "이미지 단독 수정", description = "multipart 프로필 이미지만 업로드합니다.")
    public ResponseEntity<ApiResponse<String>> updateProfileImage(
            @PathVariable Long userId,
            @RequestPart("profile_image") MultipartFile file
    ) throws Exception {

        String imageUrl = userService.updateProfileImage(userId, file);
        return ResponseEntity.ok(new ApiResponse<>("profile_image_update_success", imageUrl));
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

    @Operation(summary = "비밀번호 변경", description = "새 비밀번호를 입력하고 확인 후 변경합니다.")
    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordRequest request
    ) {
        try {
            userService.updatePassword(
                    userId,
                    request.getNewPassword(),
                    request.getNewPassword_check()
            );
            return ResponseEntity.ok(new ApiResponse<>("password_update_success", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}
