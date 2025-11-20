package com.example.assignment_4.auth;

import com.example.assignment_4.common.ApiResponse;
import com.example.assignment_4.dto.LoginRequest;
import com.example.assignment_4.dto.LoginResponse;
import com.example.assignment_4.dto.UserInfo;
import com.example.assignment_4.entity.User;
import com.example.assignment_4.repository.UserRepository;
import com.example.assignment_4.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /** ===========================
     *   🔥 1) 로그인
     * =========================== */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {

        // 1) Security 인증
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(), req.getPassword()
                )
        );

        // 2) 사용자 조회
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("user_not_found"));

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage()
        );

        // 3) 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 4) Refresh Token을 HttpOnly 쿠키에 저장
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)  // ❗ HTTPS일 때 true로 바꿔 (로컬 개발은 false)
                .path("/")
                .sameSite("Lax")  // samesite Lax 설정
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        // 5) Access Token + UserInfo 응답
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "login_success",
                        new LoginResponse(userInfo, accessToken)
                )
        );
    }

    /** ===========================
     *   🔥 2) AccessToken 재발급
     * =========================== */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(HttpServletRequest request) {

        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>("invalid_refresh_token", null));
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);

        return ResponseEntity.ok(
                new ApiResponse<>("access_token_refreshed", newAccessToken)
        );
    }

    /** ===========================
     *   🔥 3) 로그아웃 (쿠키 삭제)
     * =========================== */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>("logout_success", null));
    }

    /** ===========================
     *   🔧 Refresh Token 쿠키 추출 유틸
     * =========================== */
    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return java.util.Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refresh_token"))
                .findFirst()
                .map(jakarta.servlet.http.Cookie::getValue)
                .orElse(null);
    }
}
