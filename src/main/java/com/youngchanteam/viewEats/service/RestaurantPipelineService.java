package com.youngchanteam.viewEats.service;

import com.youngchanteam.viewEats.domain.entity.Category;
import com.youngchanteam.viewEats.domain.entity.Restaurant;
import com.youngchanteam.viewEats.domain.entity.YoutubeVideo;
import com.youngchanteam.viewEats.dto.internal.KakaoPlace;
import com.youngchanteam.viewEats.dto.response.PipelineResultResponse;
import com.youngchanteam.viewEats.dto.response.YoutubeVideoResponse;
import com.youngchanteam.viewEats.repository.CategoryRepository;
import com.youngchanteam.viewEats.repository.RestaurantRepository;
import com.youngchanteam.viewEats.repository.YoutubeVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantPipelineService {

    private final YoutubeService youtubeService;
    private final GeminiService geminiService;
    private final KakaoLocalService kakaoLocalService;
    private final RestaurantRepository restaurantRepository;
    private final YoutubeVideoRepository youtubeVideoRepository;
    private final CategoryRepository categoryRepository;

    public PipelineResultResponse collect(String keyword) throws IOException {
        List<YoutubeVideoResponse> videos = youtubeService.searchFoodVideos(keyword);
        log.info("YouTube 검색 결과: {}개 영상 (키워드: {})", videos.size(), keyword);

        int restaurantsSaved = 0;
        int videosSaved = 0;

        for (YoutubeVideoResponse video : videos) {
            if (youtubeVideoRepository.findByVideoId(video.videoId()).isPresent()) {
                log.debug("이미 처리된 영상 스킵: {}", video.videoId());
                continue;
            }

            List<String> restaurantNames = geminiService.extractRestaurantNames(
                    video.title(), video.description());

            if (restaurantNames.isEmpty()) {
                log.debug("식당명 추출 결과 없음: {}", video.title());
                continue;
            }

            log.info("추출된 식당 {}개: {} (영상: {})", restaurantNames.size(), restaurantNames, video.title());

            Restaurant linkedRestaurant = null;

            for (String restaurantName : restaurantNames) {
                try {
                    Restaurant restaurant = findOrCreateRestaurant(restaurantName);
                    if (restaurant != null) {
                        restaurantsSaved++;
                        if (linkedRestaurant == null) {
                            linkedRestaurant = restaurant;
                        }
                    }
                } catch (Exception e) {
                    log.warn("식당 저장 실패: {} / {}", restaurantName, e.getMessage());
                }
            }

            if (linkedRestaurant != null) {
                saveVideo(video, linkedRestaurant);
                videosSaved++;
            }
        }

        log.info("파이프라인 완료 - 영상: {}개 처리, 식당: {}개 저장, 영상: {}개 저장",
                videos.size(), restaurantsSaved, videosSaved);

        return new PipelineResultResponse(videos.size(), restaurantsSaved, videosSaved);
    }

    @Transactional
    protected Restaurant findOrCreateRestaurant(String restaurantName) {
        return kakaoLocalService.searchRestaurant(restaurantName)
                .map(place -> restaurantRepository
                        .findByNameAndAddress(place.name(), place.address())
                        .orElseGet(() -> saveNewRestaurant(place)))
                .orElse(null);
    }

    private Restaurant saveNewRestaurant(KakaoPlace place) {
        Category category = categoryRepository.findByName(place.category())
                .orElseGet(() -> categoryRepository.save(
                        Category.builder().name(place.category()).build()));

        return restaurantRepository.save(Restaurant.builder()
                .name(place.name())
                .address(place.address())
                .latitude(place.latitude())
                .longitude(place.longitude())
                .phone(place.phone())
                .category(category)
                .build());
    }

    @Transactional
    protected void saveVideo(YoutubeVideoResponse video, Restaurant restaurant) {
        youtubeVideoRepository.save(YoutubeVideo.builder()
                .videoId(video.videoId())
                .title(video.title())
                .channelName(video.channelName())
                .viewCount(video.viewCount())
                .thumbnailUrl(video.thumbnailUrl())
                .restaurant(restaurant)
                .build());
    }
}