package com.youngchanteam.viewEats.controller;

import com.youngchanteam.viewEats.dto.response.PipelineResultResponse;
import com.youngchanteam.viewEats.service.RestaurantPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/pipeline")
@RequiredArgsConstructor
public class PipelineController {

    private final RestaurantPipelineService pipelineService;

    @PostMapping("/collect")
    public ResponseEntity<PipelineResultResponse> collect(
            @RequestParam String keyword) throws IOException {
        PipelineResultResponse result = pipelineService.collect(keyword);
        return ResponseEntity.ok(result);
    }
}