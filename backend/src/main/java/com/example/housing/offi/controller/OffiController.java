package com.example.housing.offi.controller;

import com.example.housing.offi.service.OffiService;
import com.example.housing.recommend.dto.AptComplexSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offi")
@RequiredArgsConstructor
public class OffiController {

    private final OffiService offiService;

    @GetMapping("/complexes/area")
    public ResponseEntity<List<AptComplexSummary>> getComplexesForMap(
            @RequestParam String sigungu,
            @RequestParam(required = false) String dong) {
        return ResponseEntity.ok(offiService.getComplexesForMap(sigungu, dong));
    }
}
