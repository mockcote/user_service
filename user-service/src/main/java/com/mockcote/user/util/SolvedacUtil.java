package com.mockcote.user.util;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolvedacUtil {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Solved.ac API를 호출하고 JSON 데이터를 가공하여 반환합니다.
     *
     * @param handle Solved.ac 유저 핸들
     * @return JsonNode (가공된 JSON 데이터)
     * @throws Exception JSON 처리 중 오류 발생 시 예외를 던집니다.
     */
    public JsonNode getUserData(String handle) throws Exception {
        String url = "https://solved.ac/api/v3/user/show?handle=" + handle;

        try {
            // API 호출
            String response = restTemplate.getForObject(url, String.class);

            // JSON 문자열을 JsonNode로 변환 및 반환
            return objectMapper.readTree(response);
        } catch (Exception e) {
            // API 호출 실패 또는 JSON 처리 실패 시 예외 처리
            throw new Exception("Failed to fetch or process data from Solved.ac API", e);
        }
    }
}