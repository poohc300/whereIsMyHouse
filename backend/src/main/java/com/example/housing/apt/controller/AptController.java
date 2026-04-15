package com.example.housing.apt.controller;

import com.example.housing.apt.dto.AptComplexDto;
import com.example.housing.apt.dto.AptRentDto;
import com.example.housing.apt.dto.AptTradeDto;
import com.example.housing.apt.service.AptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apt")
@RequiredArgsConstructor
public class AptController {

    private final AptService aptService;

    // 법정동 기준 단지 목록 (지도 마커용)
    @GetMapping("/complexes")
    public ResponseEntity<List<AptComplexDto>> getComplexes(@RequestParam String bjdongCode) {
        return ResponseEntity.ok(aptService.getComplexesByDong(bjdongCode));
    }

    // 단지별 매매 실거래가
    @GetMapping("/complexes/{complexId}/trades")
    public ResponseEntity<List<AptTradeDto>> getTrades(@PathVariable Long complexId) {
        return ResponseEntity.ok(aptService.getTradesByComplex(complexId));
    }

    // 법정동 + 년월 기준 매매 실거래가
    @GetMapping("/trades")
    public ResponseEntity<List<AptTradeDto>> getTradesByDong(
            @RequestParam String bjdongCode,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(aptService.getTradesByDong(bjdongCode, year, month));
    }

    // 단지별 전월세 실거래가
    @GetMapping("/complexes/{complexId}/rents")
    public ResponseEntity<List<AptRentDto>> getRents(@PathVariable Long complexId) {
        return ResponseEntity.ok(aptService.getRentsByComplex(complexId));
    }
}
