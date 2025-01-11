package com.mockcote.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.mockcote.user.dto.HandleAuthRequest;
import com.mockcote.user.dto.LoginRequest;
import com.mockcote.user.service.UserServiceImpl;
import com.mockcote.user.util.HandleAuthUtil;
import com.mockcote.user.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;
    private final int EXPIRED_HOUR = 12 * 60 * 60;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HandleAuthUtil handleAuthUtil;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Map<String, String> tokens = userService.login(loginRequest);

            // Refresh Token 쿠키 설정
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                    .httpOnly(true)
                    .secure(true) // 개발 단계에서는 false, 배포 시 true로 설정
                    .sameSite("None") // 필요에 따라 환경 설정
                    .path("/")
                    .domain(".mockcote.site")
                    .maxAge(EXPIRED_HOUR)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            // Handle 쿠키 설정
            ResponseCookie handleCookie = ResponseCookie.from("handle", tokens.get("handle"))
            		.httpOnly(true)
            		.secure(true) // 개발 단계에서는 false, 배포 시 true로 설정
                    .sameSite("None") // 필요에 따라 환경 설정
                    .path("/")
                    .domain(".mockcote.site")
                    .maxAge(EXPIRED_HOUR)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, handleCookie.toString());

            // Level 쿠키 설정
            ResponseCookie levelCookie = ResponseCookie.from("level", tokens.get("level"))
            		.httpOnly(true)
            		.secure(true) // 개발 단계에서는 false, 배포 시 true로 설정
                    .sameSite("None") // 필요에 따라 환경 설정
                    .path("/")
                    .domain(".mockcote.site")
                    .maxAge(EXPIRED_HOUR)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, levelCookie.toString());

            tokens.remove("refreshToken");
            return ResponseEntity.ok(tokens);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                         HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("No Refresh Token found");
        }

        Claims claim = jwtUtil.getClaimFromRefreshToken(refreshToken);
        String userId = (String) claim.get("userId");

        // DB에서 리프레시 토큰 삭제
        userService.deleteRefreshToken(userId);

        // 쿠키 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
        		.httpOnly(true)
        		.secure(true)
                .sameSite("None")
                .domain(".mockcote.site")
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        ResponseCookie handleCookie = ResponseCookie.from("handle", "")
        		.httpOnly(true)
        		.secure(true)
                .sameSite("None")
                .domain(".mockcote.site")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, handleCookie.toString());

        ResponseCookie levelCookie = ResponseCookie.from("level", "")
        		.httpOnly(true)
        		.secure(true)
                .sameSite("None")
                .domain(".mockcote.site")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, levelCookie.toString());

        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    // 리프레시 토큰으로 액세스 토큰 재발급
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("No Refresh Token found");
        }

        if (jwtUtil.validateRefreshToken(refreshToken)) {
            Map<String, Object> response = new HashMap<>();

            Claims claim = jwtUtil.getClaimFromRefreshToken(refreshToken);
            String userId = (String) claim.get("userId");
            String handle = claim.getSubject();

            // 액세스 토큰 재발급
            String accessToken = jwtUtil.generateToken(userId, handle);
            response.put("accessToken", accessToken);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid Refresh Token");
        }
    }

    // 사용자 레벨 조회
    @GetMapping("/level/{handle}")
    public ResponseEntity<?> getLevel(@PathVariable("handle") String handle) {
        JsonNode data = userService.getSolvedData(handle);
        if (data == null) {
            return ResponseEntity.badRequest().body("Invalid handle");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    // Handle 인증
    @PostMapping("/handle-auth")
    public ResponseEntity<String> handleAuth(@RequestBody HandleAuthRequest handleAuthRequest) {
        boolean res = handleAuthUtil.validateSubmission(handleAuthRequest);
        if (res) {
            return ResponseEntity.ok("검증 완료 되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("검증에 실패했습니다. 제출 내역을 확인해주세요.");
        }
    }
}
