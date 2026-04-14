package com.youngchanteam.viewEats.dto.response;

public record PipelineResultResponse(
        int videosProcessed,
        int restaurantsSaved,
        int videosSaved
) {}