package com.youngchanteam.viewEats.dto.response;

import com.youngchanteam.viewEats.domain.entity.YoutubeVideo;
import lombok.Builder;

@Builder
public record YoutubeVideoResponse(
        Long id,
        String videoId,
        String title,
        String channelName,
        Long viewCount,
        String thumbnailUrl,
        String youtubeUrl,
        String description
) {
    public static YoutubeVideoResponse from(YoutubeVideo video) {
        return YoutubeVideoResponse.builder()
                .id(video.getId())
                .videoId(video.getVideoId())
                .title(video.getTitle())
                .channelName(video.getChannelName())
                .viewCount(video.getViewCount())
                .thumbnailUrl(video.getThumbnailUrl())
                .youtubeUrl("https://www.youtube.com/watch?v=" + video.getVideoId())
                .build();
    }
}