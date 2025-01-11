package com.mockcote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 보호 기능 비활성화(REST API)
                .csrf(csrf -> csrf.disable())

                // 요청 경로에 대한 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**", "/auth/**").permitAll() // 특정 경로 허용
                        .anyRequest().permitAll() // 나머지 요청도 허용
                )
                .build();
    }
}
