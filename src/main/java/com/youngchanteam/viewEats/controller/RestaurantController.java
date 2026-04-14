package com.youngchanteam.viewEats.controller;

import com.youngchanteam.viewEats.dto.response.RestaurantDetailResponse;
import com.youngchanteam.viewEats.dto.response.RestaurantMarkerResponse;
import com.youngchanteam.viewEats.dto.response.RestaurantSearchResponse;
import com.youngchanteam.viewEats.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 지도 영역 내 마커 조회
    @GetMapping("/markers")
    public ResponseEntity<List<RestaurantMarkerResponse>> getMarkers(
            @RequestParam Double swLat,
            @RequestParam Double neLat,
            @RequestParam Double swLng,
            @RequestParam Double neLng
    ) {
        return ResponseEntity.ok(restaurantService.getMarkersByBounds(swLat, neLat, swLng, neLng));
    }

    // 맛집 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDetailResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getDetail(id));
    }

    // 카테고리별 마커 조회
    @GetMapping("/markers/category/{categoryId}")
    public ResponseEntity<List<RestaurantMarkerResponse>> getMarkersByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(restaurantService.getMarkersByCategory(categoryId));
    }

    // 키워드 검색 (프론트 연동용)
    @GetMapping("/search")
    public ResponseEntity<java.util.Map<String, List<RestaurantSearchResponse>>> search(
            @RequestParam(defaultValue = "") String query
    ) {
        List<RestaurantSearchResponse> results = restaurantService.search(query);
        return ResponseEntity.ok(java.util.Map.of("restaurants", results));
    }
}