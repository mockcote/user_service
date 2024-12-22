package com.mockcote.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockcote.user.dto.JoinRequest;
import com.mockcote.user.dto.User;
import com.mockcote.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
    @GetMapping("/hello")
    public String hello(@RequestHeader(value = "X-Authenticated-User", required = false) String username,
                       HttpServletRequest request) {
        // 디버깅용 로그 추가
        System.out.println("Received Header - X-Authenticated-User: " + username);
        System.out.println("Incoming request from: " + request.getRemoteAddr());

        // 헤더가 없는 경우 처리
        if (username == null) {
            System.out.println("Unauthorized access - X-Authenticated-User header is missing.");
            return "Unauthorized: User header not found";
        }

        // 성공 응답
        System.out.println("Authorized user: " + username);
        return "Hello, " + username + "!";
    }
    
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinRequest join){
    	try {
			int cnt = userService.join(join);
			
			if(cnt == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입에 실패하였습니다.");
			} 
			
			return ResponseEntity.ok("회원가입 성공");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입에 실패하였습니다.");
		}
    }
    
    @GetMapping("/find/all")
    public ResponseEntity<?> findAll() {
    	Map<String, Object> response = new HashMap<>();
    	
    	List<User> list = userService.selectAll();
    	response.put("userlist", list);
    	
    	return ResponseEntity.ok(response);
    }
    
    @GetMapping("/find/{handle}")
    public ResponseEntity<?> findUser(@PathVariable String handle) {
    	User user = userService.selectUser(handle);
    	
    	return ResponseEntity.ok(user);
    }
    
    
}
