package com.mockcote.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final UserServiceImpl userService;
	
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HandleAuthUtil handleAuthUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        try {
            // UserService로 로그인 검증
        	Map<String, String> tokens = userService.login(loginRequest);
        	
        	Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        	refreshTokenCookie.setHttpOnly(true); // JavaScript 접근 금지
    	    refreshTokenCookie.setSecure(true); // HTTPS에서만 전송 (HTTPS 환경에서 설정)
    	    refreshTokenCookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
    	    refreshTokenCookie.setMaxAge(12 * 60 * 60); // 12시간 (리프레시 토큰 만료 시간과 일치)
    	    response.addCookie(refreshTokenCookie);
    	    
    	    Cookie handleCookie = new Cookie("handle", tokens.get("handle"));
    	    handleCookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
    	    handleCookie.setMaxAge(12 * 60 * 60); // 12시간 (리프레시 토큰 만료 시간과 일치)
    	    response.addCookie(handleCookie);
    	    
    	    Cookie levelCookie = new Cookie("level", tokens.get("level"));
    	    levelCookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
    	    levelCookie.setMaxAge(12 * 60 * 60); // 12시간 (리프레시 토큰 만료 시간과 일치)
    	    response.addCookie(levelCookie);
    	    
    	    tokens.remove("refreshToken");
    	    
    	    return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            // 검증 실패 시 401 응답
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
	public ResponseEntity<String> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, 
	                                     HttpServletResponse response) {
    	
    	Claims claim = jwtUtil.getClaimFromRefreshToken(refreshToken);
		String userId = (String) claim.get("userId");
		
	    if (refreshToken != null) {
	        // DB에서 리프레시 토큰 삭제
	        userService.deleteRefreshToken(userId);
	    }

	    // 쿠키 삭제
	    Cookie refreshTokenCookie = new Cookie("refreshToken", null);
	    refreshTokenCookie.setHttpOnly(true);
	    refreshTokenCookie.setSecure(true);
	    refreshTokenCookie.setPath("/");
	    refreshTokenCookie.setMaxAge(0); // 쿠키 즉시 만료
	    response.addCookie(refreshTokenCookie);
	    
	    Cookie handleCookie = new Cookie("handle", null);
	    handleCookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
	    handleCookie.setMaxAge(0); // 12시간 (리프레시 토큰 만료 시간과 일치)
	    response.addCookie(handleCookie);

	    Cookie levelCookie = new Cookie("level", null);
	    levelCookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
	    levelCookie.setMaxAge(0); // 12시간 (리프레시 토큰 만료 시간과 일치)
	    response.addCookie(levelCookie);

	    return ResponseEntity.ok("로그아웃이 완료되었습니다.");
	}
    
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken") String refreshToken) {
    	if(refreshToken == null) {
    		return ResponseEntity.status(401).body("No Refresh Token found");
    	}
    	
    	if(jwtUtil.validateRefreshToken(refreshToken)) {
    		Map<String, Object> response = new HashMap<>();
    		
    		Claims claim = jwtUtil.getClaimFromRefreshToken(refreshToken);
    		String userId = (String) claim.get("userId");
    		String handle = claim.getSubject();
    		
    		String accessToken = jwtUtil.generateToken(userId, handle);
    		response.put("accessToken", accessToken);
    		
    		System.out.println("토큰 재발급");
    		return ResponseEntity.ok(response);
    	} else {
    		return ResponseEntity.status(401).body("invalid refresh token");
    	}
    }
    
    @GetMapping("/level/{handle}")
    public ResponseEntity<?> getLevel(@PathVariable("handle") String handle) {
    	JsonNode data = userService.getSolvedData(handle);
    	if(data == null) return ResponseEntity.badRequest().body("invalid handle");
    	
    	Map<String, Object> response = new HashMap<>();
    	response.put("data", data);
    	
    	System.out.println("data: " + data.get("tier"));
    	
    	return ResponseEntity.ok(response);

    }
    
    @PostMapping("/handle-auth")
    public ResponseEntity<String> handleAuth(@RequestBody HandleAuthRequest handleAuthRequest) {
        boolean res = handleAuthUtil.validateSubmission(handleAuthRequest);
        if (res) {
            return ResponseEntity.ok("검증 완료 되었습니다.");
        } else {
            // 검증 실패 시 400 Bad Request 상태 코드와 함께 실패 메시지 반환
            return ResponseEntity.badRequest().body("검증에 실패했습니다. 제출 내역을 확인해주세요.");
        }

    }
}
