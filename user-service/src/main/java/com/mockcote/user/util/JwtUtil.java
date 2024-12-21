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
	private final long expirationTime;

	public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration:3600000}") long expiration) { // 기본값
																													// 1시간
		this.secretKey = Base64.getDecoder().decode(secret);
		this.expirationTime = expiration;
	}

	public String generateToken(String username) {
	    System.out.println("Generating token for user: " + username);
	    return Jwts.builder()
	            .setSubject(username)
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
	            .signWith(Keys.hmacShaKeyFor(secretKey), SignatureAlgorithm.HS256)
	            .compact();
	}

}
