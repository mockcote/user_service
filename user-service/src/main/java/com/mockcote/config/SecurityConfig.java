package com.mockcote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

public class SecurityConfig {
	
	// Spring Security의 필터 체인을 정의하는 메서드
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // CSRF 보호 기능 비활성화(REST API)
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                    .pathMatchers("**").permitAll()
                    .anyExchange().permitAll()
                )
                .build();
    }
}
