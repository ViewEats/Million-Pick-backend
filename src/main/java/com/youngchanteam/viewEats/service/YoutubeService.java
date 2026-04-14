package com.youngchanteam.viewEats.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.youngchanteam.viewEats.dto.response.YoutubeVideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final YouTube youtube;

    @Value("${youtube.api-key}")
    private String apiKey;

    private static final long MIN_VIEW_COUNT = 1_000_000L;

    // 키워드로 맛집 영상 검색 (조회수 100만 이상 필터링)
    public List<YoutubeVideoResponse> searchFoodVideos(String keyword) throws IOException {
        List<SearchResult> searchResults = youtube.search()
                .list(List.of("id", "snippet"))
                .setKey(apiKey)
                .setQ(keyword + " 맛집")
                .setType(List.of("video"))
                .setMaxResults(10L)
                .execute()
                .getItems();

        List<String> videoIds = searchResults.stream()
                .map(r -> r.getId().getVideoId())
                .toList();

        return getVideoDetails(videoIds);
    }

    // videoId 리스트로 상세 정보(조회수 포함) 조회 후 100만 이상만 반환
    public List<YoutubeVideoResponse> getVideoDetails(List<String> videoIds) throws IOException {
        List<Video> videos = youtube.videos()
                .list(List.of("id", "snippet", "statistics"))
                .setKey(apiKey)
                .setId(videoIds)
                .execute()
                .getItems();

        return videos.stream()
                .filter(v -> {
                    var count = v.getStatistics().getViewCount();
                    return count != null && count.longValue() >= MIN_VIEW_COUNT;
                })
                .map(v -> YoutubeVideoResponse.builder()
                        .videoId(v.getId())
                        .title(v.getSnippet().getTitle())
                        .channelName(v.getSnippet().getChannelTitle())
                        .viewCount(v.getStatistics().getViewCount().longValue())
                        .thumbnailUrl(v.getSnippet().getThumbnails().getHigh().getUrl())
                        .youtubeUrl("https://www.youtube.com/watch?v=" + v.getId())
                        .description(v.getSnippet().getDescription())
                        .build())
                .toList();
    }
}