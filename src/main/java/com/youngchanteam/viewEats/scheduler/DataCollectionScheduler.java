package com.youngchanteam.viewEats.scheduler;

import com.youngchanteam.viewEats.service.RestaurantPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataCollectionScheduler {

    private final RestaurantPipelineService pipelineService;

    // YouTube 10,000 units/일, Gemini 1,500회/일 한도 내로 유지
    // 키워드 11개 × 영상 10개 = 최대 Gemini 110회/일 (안전)
    private static final List<String> KEYWORDS = List.of(
            // 서울 주요 상권
            "홍대 맛집",
            "강남 맛집",
            "이태원 맛집",
            "성수 맛집",
            // 수도권
            "인천 맛집",
            // 충청/전라
            "전주 맛집",
            "대전 맛집",
            // 경상
            "부산 맛집",
            "대구 맛집",
            // 관광지
            "제주 맛집",
            "경주 맛집"
    );

    // 매일 오후 8시 실행
    @Scheduled(cron = "0 10 22 * * *")
    public void collectRestaurantData() {
        runCollection();
    }

    private void runCollection() {
        log.info("=== 맛집 데이터 자동 수집 시작 ===");

        for (String keyword : KEYWORDS) {
            try {
                var result = pipelineService.collect(keyword);
                log.info("키워드 '{}' 완료 - 식당 {}개 저장", keyword, result.restaurantsSaved());
            } catch (Exception e) {
                log.error("키워드 '{}' 수집 실패: {}", keyword, e.getMessage());
            }
        }

        log.info("=== 맛집 데이터 자동 수집 완료 ===");
    }
}