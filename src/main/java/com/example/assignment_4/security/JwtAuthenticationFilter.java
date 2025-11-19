package com.example.assignment_4.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ⭐ 1) Access Token은 Authorization 헤더로 받는다
        String token = resolveAccessToken(request);

        // ⭐ 2) Access Token이 유효하면 인증 처리
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // ⭐ 3) 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * ⭐ Authorization: Bearer <accessToken>
     * Refresh Token은 여기서 읽지 않는다 (쿠키 전용)
     */
    private String resolveAccessToken(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 이후 값
        }

        return null;
    }
}
