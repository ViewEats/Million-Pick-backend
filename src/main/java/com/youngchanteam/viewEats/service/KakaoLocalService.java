package com.youngchanteam.viewEats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngchanteam.viewEats.dto.internal.KakaoPlace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
public class KakaoLocalService {

    // FD6 = 음식점 카테고리 그룹 코드
    private static final String KAKAO_KEYWORD_URL =
            "https://dapi.kakao.com/v2/local/search/keyword.json?query={query}&size=1&category_group_code=FD6";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.api-key}")
    private String apiKey;

    public Optional<KakaoPlace> searchRestaurant(String restaurantName) {
        try {
            String response = restClient.get()
                    .uri(KAKAO_KEYWORD_URL, restaurantName)
                    .header("Authorization", "KakaoAK " + apiKey)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode documents = root.get("documents");

            if (documents == null || documents.isEmpty()) {
                log.debug("카카오 검색 결과 없음: {}", restaurantName);
                return Optional.empty();
            }

            JsonNode doc = documents.get(0);
            String categoryName = doc.path("category_name").asText("기타");

            KakaoPlace place = new KakaoPlace(
                    doc.get("place_name").asText(),
                    doc.get("address_name").asText(),
                    doc.path("phone").asText(null),
                    doc.get("y").asDouble(),   // latitude
                    doc.get("x").asDouble(),   // longitude
                    extractSubCategory(categoryName)
            );

            return Optional.of(place);
        } catch (Exception e) {
            log.warn("카카오 로컬 API 호출 실패 - 식당명: {} / 오류: {}", restaurantName, e.getMessage());
            return Optional.empty();
        }
    }

    // "음식점 > 한식 > 백반/한정식" → "한식"
    private String extractSubCategory(String categoryName) {
        String[] parts = categoryName.split(" > ");
        return parts.length >= 2 ? parts[1] : "기타";
    }
}