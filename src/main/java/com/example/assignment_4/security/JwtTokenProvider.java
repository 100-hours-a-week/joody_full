package com.example.assignment_4.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final CustomUserDetailsService customUserDetailsService;

    // 🔥 강력한 시크릿 키
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // =============================
    // 🔥 토큰 만료시간
    // =============================
    private final long ACCESS_TOKEN_EXPIRE = 1000L * 60 * 30;        // 30분
    private final long REFRESH_TOKEN_EXPIRE = 1000L * 60 * 60 * 24 * 7; // 7일

    // =============================
    // 🔥 Access Token 생성 (userId)
    // =============================
    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // userId 저장
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE))
                .claim("type", "access")
                .signWith(secretKey)
                .compact();
    }

    // =============================
    // 🔥 Refresh Token 생성 (userId)
    // =============================
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE))
                .claim("type", "refresh")
                .signWith(secretKey)
                .compact();
    }

    // =============================
    // 🔥 토큰에서 userId 추출
    // =============================
    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // =============================
    // 🔥 토큰 유효성 검증
    // =============================
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // =============================
    // 🔥 Access Token → 인증 정보 생성
    // =============================
    public Authentication getAuthentication(String token) {
        Long userId = getUserId(token);
        UserDetails userDetails =
                customUserDetailsService.loadUserById(userId);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
