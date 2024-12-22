package com.mockcote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	// Spring Security의 필터 체인을 정의하는 메서드
    @Bean
    public SecurityFilterChain  securityWebFilterChain(HttpSecurity  http) throws Exception {
        return http
                // CSRF 보호 기능 비활성화(REST API)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                		.requestMatchers("/user/**","/auth/**").permitAll()
                		.anyRequest().permitAll()).build();
    }
}
