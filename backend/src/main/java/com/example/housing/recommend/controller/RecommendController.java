package com.example.housing.recommend.controller;

import com.example.housing.recommend.dto.RecommendRequest;
import com.example.housing.recommend.dto.RecommendResponse;
import com.example.housing.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * POST /api/recommend
     * 인증 불필요. 요청 데이터는 DB에 저장되지 않는다.
     */
    @PostMapping
    public ResponseEntity<RecommendResponse> recommend(@RequestBody RecommendRequest request) {
        return ResponseEntity.ok(recommendService.recommend(request));
    }
}
