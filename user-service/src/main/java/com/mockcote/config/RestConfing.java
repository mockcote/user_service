package com.mockcote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfing {
	
	@Bean
    public RestTemplate restTemplate() {
		return new RestTemplate();
    }
	
}
