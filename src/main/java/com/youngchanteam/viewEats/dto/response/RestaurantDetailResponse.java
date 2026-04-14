package com.youngchanteam.viewEats.dto.response;

import com.youngchanteam.viewEats.domain.entity.Restaurant;
import lombok.Builder;

import java.util.List;

@Builder
public record RestaurantDetailResponse(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String phone,
        String categoryName,
        List<YoutubeVideoResponse> videos
) {
    public static RestaurantDetailResponse from(Restaurant restaurant) {
        return RestaurantDetailResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .phone(restaurant.getPhone())
                .categoryName(restaurant.getCategory() != null ? restaurant.getCategory().getName() : null)
                .videos(restaurant.getVideos().stream()
                        .map(YoutubeVideoResponse::from)
                        .toList())
                .build();
    }
}