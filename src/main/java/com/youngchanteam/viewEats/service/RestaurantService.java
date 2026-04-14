package com.youngchanteam.viewEats.service;

import com.youngchanteam.viewEats.dto.response.RestaurantDetailResponse;
import com.youngchanteam.viewEats.dto.response.RestaurantMarkerResponse;
import com.youngchanteam.viewEats.dto.response.RestaurantSearchResponse;
import com.youngchanteam.viewEats.exception.RestaurantNotFoundException;
import com.youngchanteam.viewEats.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantMarkerResponse> getMarkersByBounds(Double swLat, Double neLat,
                                                              Double swLng, Double neLng) {
        return restaurantRepository.findByBounds(swLat, neLat, swLng, neLng).stream()
                .map(RestaurantMarkerResponse::from)
                .toList();
    }

    public RestaurantDetailResponse getDetail(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(RestaurantDetailResponse::from)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    public List<RestaurantMarkerResponse> getMarkersByCategory(Long categoryId) {
        return restaurantRepository.findByCategory_Id(categoryId).stream()
                .map(RestaurantMarkerResponse::from)
                .toList();
    }

    public List<RestaurantSearchResponse> search(String keyword) {
        return restaurantRepository.searchByKeyword(keyword).stream()
                .flatMap(r -> RestaurantSearchResponse.from(r).stream())
                .toList();
    }
}