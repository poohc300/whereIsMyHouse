package com.example.housing.subscription.controller;

import com.example.housing.subscription.dto.SubscriptionAnnouncementDto;
import com.example.housing.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // 현재 청약 접수 중인 공고 (지도 마커용)
    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionAnnouncementDto>> getActive() {
        return ResponseEntity.ok(subscriptionService.getActive());
    }

    // 법정동 기준 분양공고 목록
    @GetMapping
    public ResponseEntity<List<SubscriptionAnnouncementDto>> getByDong(@RequestParam String bjdongCode) {
        return ResponseEntity.ok(subscriptionService.getByDong(bjdongCode));
    }
}
