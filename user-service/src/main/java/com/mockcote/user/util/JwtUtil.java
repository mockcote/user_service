package com.mockcote.user.util;

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
            @Value("${jwt.accessExpiration:3600000}") long accessExpiration,  // 기본값: 1시간
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
}
