package com.mockcote.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

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
}
