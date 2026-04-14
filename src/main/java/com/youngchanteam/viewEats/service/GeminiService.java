package com.youngchanteam.viewEats.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";
    private static final int MAX_DESCRIPTION_LENGTH = 2000;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api-key}")
    private String apiKey;

    public List<String> extractRestaurantNames(String title, String description) {
        String truncatedDescription = description != null
                ? description.substring(0, Math.min(description.length(), MAX_DESCRIPTION_LENGTH))
                : "";

        String prompt = """
                다음 유튜브 영상의 제목과 설명에서 실제 식당/음식점 고유 이름만 추출해줘.

                추출 규칙:
                - 설명의 타임스탬프 줄(예: "00:00 홍길동식당", "▶ 맛있는집")에서 식당명을 우선 추출해
                - 제목에서도 고유 식당명이 있으면 추출해
                - 체인점(맥도날드, 스타벅스, 교촌치킨 등) 제외
                - 일반 명사(맛집, 식당, 음식점, 레스토랑 등) 제외
                - 지역명만 있고 식당명이 없으면 제외 (예: "강남 맛집" 제외)

                JSON 배열 형식으로만 응답해. 예: ["홍길동식당", "맛있는집"]
                식당 이름이 없으면 빈 배열 []로만 응답해.

                제목: %s
                설명: %s
                """.formatted(title, truncatedDescription);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of("maxOutputTokens", 512)
        );

        try {
            Thread.sleep(3000); // 503 과부하 방지용 딜레이

            String response = restClient.post()
                    .uri(GEMINI_API_URL + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            String text = root.get("candidates").get(0)
                    .get("content").get("parts").get(0)
                    .get("text").asText().trim();

            // JSON 배열 부분만 추출 (Gemini가 설명을 앞에 붙이는 경우 대비)
            int start = text.indexOf('[');
            int end = text.lastIndexOf(']');
            if (start == -1 || end == -1) {
                return List.of();
            }
            String jsonArray = text.substring(start, end + 1);

            return objectMapper.readValue(jsonArray, new TypeReference<List<String>>() {});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        } catch (Exception e) {
            log.warn("Gemini API 식당명 추출 실패 - 영상: {} / 오류: {}", title, e.getMessage());
            return List.of();
        }
    }
}