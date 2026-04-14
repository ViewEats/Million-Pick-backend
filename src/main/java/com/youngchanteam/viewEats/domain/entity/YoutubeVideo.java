package com.youngchanteam.viewEats.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "youtube_videos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YoutubeVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String videoId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String channelName;

    @Column(nullable = false)
    private Long viewCount;

    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Builder
    public YoutubeVideo(String videoId, String title, String channelName,
                        Long viewCount, String thumbnailUrl, Restaurant restaurant) {
        this.videoId = videoId;
        this.title = title;
        this.channelName = channelName;
        this.viewCount = viewCount;
        this.thumbnailUrl = thumbnailUrl;
        this.restaurant = restaurant;
    }
}