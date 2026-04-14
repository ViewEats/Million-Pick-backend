package com.youngchanteam.viewEats.dto.internal;

public record KakaoPlace(
        String name,
        String address,
        String phone,
        Double latitude,
        Double longitude,
        String category
) {}