package com.youngchanteam.viewEats.repository;

import com.youngchanteam.viewEats.domain.entity.YoutubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, Long> {

    Optional<YoutubeVideo> findByVideoId(String videoId);

    List<YoutubeVideo> findByRestaurant_Id(Long restaurantId);

    List<YoutubeVideo> findByViewCountGreaterThanEqualOrderByViewCountDesc(Long minViewCount);
}