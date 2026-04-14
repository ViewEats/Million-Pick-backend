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
    // 키워드 5개 × 영상 10개 = 최대 Gemini 50회/일 (안전)
    private static final List<String> KEYWORDS = List.of(
            "서울 맛집",
            "홍대 맛집",
            "강남 맛집",
            "부산 맛집",
            "제주 맛집"
    );

    // 매일 오후 10시 실행
    @Scheduled(cron = "0 0 22 * * *")
    public void collectRestaurantData() {
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