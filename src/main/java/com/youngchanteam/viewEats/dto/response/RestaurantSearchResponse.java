package com.youngchanteam.viewEats.dto.response;

import com.youngchanteam.viewEats.domain.entity.Restaurant;
import com.youngchanteam.viewEats.domain.entity.YoutubeVideo;

import java.util.List;

public record RestaurantSearchResponse(
        Long id,
        String name,
        String address,
        Double lat,
        Double lng,
        String videoId,
        String videoTitle,
        String videoUrl,
        String thumbnail,
        String channelTitle,
        Long viewCount
) {
    public static List<RestaurantSearchResponse> from(Restaurant restaurant) {
        List<YoutubeVideo> videos = restaurant.getVideos();
        if (videos.isEmpty()) {
            return List.of();
        }
        return videos.stream()
                .map(video -> new RestaurantSearchResponse(
                        restaurant.getId(),
                        restaurant.getName(),
                        restaurant.getAddress(),
                        restaurant.getLatitude(),
                        restaurant.getLongitude(),
                        video.getVideoId(),
                        video.getTitle(),
                        "https://www.youtube.com/watch?v=" + video.getVideoId(),
                        video.getThumbnailUrl(),
                        video.getChannelName(),
                        video.getViewCount()
                ))
                .toList();
    }
}