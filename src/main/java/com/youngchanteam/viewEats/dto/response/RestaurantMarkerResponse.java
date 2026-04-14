package com.youngchanteam.viewEats.dto.response;

import com.youngchanteam.viewEats.domain.entity.Restaurant;
import lombok.Builder;

@Builder
public record RestaurantMarkerResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude,
        String categoryName,
        int videoCount
) {
    public static RestaurantMarkerResponse from(Restaurant restaurant) {
        return RestaurantMarkerResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .categoryName(restaurant.getCategory() != null ? restaurant.getCategory().getName() : null)
                .videoCount(restaurant.getVideos().size())
                .build();
    }
}