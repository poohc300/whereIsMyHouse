package com.example.housing.apt.controller;

import com.example.housing.apt.dto.AptComplexDto;
import com.example.housing.apt.dto.AptRentDto;
import com.example.housing.apt.dto.AptTradeDto;
import com.example.housing.apt.dto.NearbyComplexDto;
import com.example.housing.apt.service.AptService;
import com.example.housing.recommend.dto.AptComplexSummary;
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

    // 위경도 기반 반경 내 단지 + 최근 1년 평균 실거래가 조회
    // 예: GET /api/apt/complexes/nearby?lat=37.5665&lng=126.9780&radiusKm=1.5
    @GetMapping("/complexes/nearby")
    public ResponseEntity<List<NearbyComplexDto>> getNearbyComplexes(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1.0") double radiusKm) {
        return ResponseEntity.ok(aptService.getNearbyComplexes(lat, lng, radiusKm));
    }

    // 시군구(+선택적 동) 기준 단지 목록 + 최근 1년 평균 실거래가 (지도 마커용)
    // 예: GET /api/apt/complexes/area?sigungu=마포구
    //     GET /api/apt/complexes/area?sigungu=마포구&dong=서교동
    @GetMapping("/complexes/area")
    public ResponseEntity<List<AptComplexSummary>> getComplexesForMap(
            @RequestParam String sigungu,
            @RequestParam(required = false) String dong) {
        return ResponseEntity.ok(aptService.getComplexesForMap(sigungu, dong));
    }

    // 시군구에 속한 법정동 목록 (읍/면/동 드롭다운용)
    // 예: GET /api/apt/dongs?sigungu=마포구
    @GetMapping("/dongs")
    public ResponseEntity<List<String>> getDongs(@RequestParam String sigungu) {
        return ResponseEntity.ok(aptService.getDongsBySigungu(sigungu));
    }
}
