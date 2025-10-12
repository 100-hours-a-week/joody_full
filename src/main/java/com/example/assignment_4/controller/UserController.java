package com.example.assignment_4.controller;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.PasswordRequest;
import com.example.assignment_4.dto.*;
import com.example.assignment_4.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인 입력 검증 API
     * POST /users/login/validate
     */
    @PostMapping("/login/validate")
    public ResponseEntity<ApiResponse<Void>> validateLogin(
            @Valid @RequestBody LoginValidateRequest req
    ) {
        boolean valid = userService.validateCredentials(req.getEmail(), req.getPassword());
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("invalid_credentials", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("validation_success", null));
    }

    /**
     * 로그인 처리 API
     * POST /users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest req
    ) {
        try {
            var loginResponse = userService.login(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new ApiResponse<>("login_success", loginResponse));
        } catch (RuntimeException ex) {
            if ("invalid_credentials".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>("invalid_credentials", null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    /*회원가입*/
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(
            @Valid @RequestBody SignupRequest req
    ) {
        try {
            Long userId = userService.signup(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("register_success", Map.of("user_id", userId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("invalid_request", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>("duplicate_email_or_nickname", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    /* 회원정보 수정 */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        try {
            userService.updateProfile(req);
            return ResponseEntity.ok(new ApiResponse<>("update_success", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("invalid_request", null));
        } catch (IllegalStateException e) {
            if ("duplicate_nickname".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>("duplicate_nickname", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("invalid_request", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }



    /* 회원탈퇴 */
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<?>> withdraw() {
        try {
            userService.withdrawUser();
            return ResponseEntity.ok(new ApiResponse<>("withdraw_success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getProfile() {
        try {
            // 실제로는 로그인한 사용자 ID를 이용해서 DB에서 조회해야 함
            // 여기서는 단순히 예시로 '탈퇴 여부'를 체크하는 로직이라고 가정
            var user = userService.getUserProfile();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("user_not_found", null));
            }
            return ResponseEntity.ok(new ApiResponse<>("profile_found", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    /*프로필 이미지 업로드 및 삭제*/

    @PostMapping("/profile/image")
    public ResponseEntity<ApiResponse<?>> uploadProfileImage(
            @RequestParam("profile_image") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>("invalid_request", null));
            }

            String uploadDir = "uploads";
            Files.createDirectories(Paths.get(uploadDir)); // 폴더 없으면 생성

            String filename = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, file.getBytes());

            // 서버에서 접근 가능한 URL (로컬 개발 환경 기준)
            String imageUrl = "http://localhost:8080/uploads/" + filename;

            userService.updateProfileImage(imageUrl);

            return ResponseEntity.ok(
                    new ApiResponse<>("upload_success", Map.of("image_url", imageUrl))
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }

    @DeleteMapping("/profile/image")
    public ResponseEntity<ApiResponse<?>> deleteProfileImage() {
        try {
            userService.deleteProfileImage();
            return ResponseEntity.ok(new ApiResponse<>("delete_success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }


    /*  비밀번호 입력 검증 및 비밀번호 수정 */
    @PostMapping("/password/validate")
    public ResponseEntity<ApiResponse<?>> validatePassword(
            @Valid @RequestBody PasswordRequest req
    ) {
        if (!req.getPassword().equals(req.getPassword_check())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ApiResponse<>("password_mismatch", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("validation_success", null));
    }


    @PutMapping("/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @Valid @RequestBody PasswordRequest req
    ) {
        try {
            if (!req.getPassword().equals(req.getPassword_check())) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(new ApiResponse<>("password_mismatch", null));
            }

            userService.updatePassword(req.getPassword());
            return ResponseEntity.ok(new ApiResponse<>("update_success", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("invalid_request", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("internal_server_error", null));
        }
    }



}
