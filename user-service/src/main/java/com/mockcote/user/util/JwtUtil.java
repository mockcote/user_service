package com.mockcote.user.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Base64;

@Component
public class JwtUtil {

    private final byte[] secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.accessExpiration:60000}") long accessExpiration,  // 기본값: 1시간
            @Value("${jwt.refreshExpiration:43200000}") long refreshExpiration // 기본값: 12시간
    ) {
        this.secretKey = Base64.getDecoder().decode(secret);
        this.accessExpirationTime = accessExpiration;
        this.refreshExpirationTime = refreshExpiration;
    }

    /**
     * 액세스 토큰 생성
     */
    public String generateToken(String userId,String handle) {
        System.out.println("Generating access token for user: " + userId);
        return Jwts.builder()
                .setSubject(handle)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(Keys.hmacShaKeyFor(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     */
    public String generateRefreshToken(String userId,String handle) {
        System.out.println("Generating refresh token for user: " + userId);
        return Jwts.builder()
                .setSubject(handle)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .claim("userId", userId)
                .claim("type", "refresh") // 토큰 유형 구분을 위한 클레임 추가 (선택)
                .signWith(Keys.hmacShaKeyFor(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // JWT 유효성 검증 메서드
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder() // JWT 파서 빌더 생성
                .setSigningKey(secretKey) // 서명 검증에 사용할 키 설정
                .build() // 파서 빌드
                .parseClaimsJws(refreshToken); // **JWT 파싱 및 서명 검증**
            return true; // 유효한 토큰인 경우 true 반환
        } catch (Exception e) {
            System.err.println("JWT validation failed: " + e.getMessage()); // 검증 실패 시 에러 메시지 출력
            return false; // **유효하지 않은 토큰 처리**
        }
    }
    
    public Claims getClaimFromRefreshToken(String refreshToken) {
    	try {
            return Jwts.parserBuilder() // JWT 파서 빌더 생성
                       .setSigningKey(secretKey) // 서명 검증에 사용할 키 설정
                       .build() // 파서 빌드
                       .parseClaimsJws(refreshToken) // JWT 파싱
                       .getBody(); // **JWT의 Payload에서 클레임 추출**
        } catch (Exception e) {
            System.err.println("Failed to extract claims from token: " + e.getMessage()); // 클레임 추출 실패 시 에러 메시지 출력
            return null; // **비정상적인 토큰에 대한 처리**
        }
    }
}
