package com.example.housing.apt.service;

import com.example.housing.apt.dto.AptComplexDto;
import com.example.housing.apt.dto.AptRentDto;
import com.example.housing.apt.dto.AptTradeDto;
import com.example.housing.apt.dto.NearbyComplexDto;
import com.example.housing.apt.mapper.AptMapper;
import com.example.housing.recommend.dto.AptComplexSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AptService {

    private final AptMapper aptMapper;

    public List<AptComplexDto> getComplexesByDong(String bjdongCode) {
        return aptMapper.findComplexesByBjdong(bjdongCode).stream()
                .map(AptComplexDto::new)
                .toList();
    }

    public List<AptTradeDto> getTradesByComplex(Long complexId) {
        return aptMapper.findTradesByComplexId(complexId).stream()
                .map(AptTradeDto::new)
                .toList();
    }

    public List<AptTradeDto> getTradesByDong(String bjdongCode, int year, int month) {
        return aptMapper.findTradesByBjdong(bjdongCode, year, month).stream()
                .map(AptTradeDto::new)
                .toList();
    }

    public List<AptRentDto> getRentsByComplex(Long complexId) {
        return aptMapper.findRentsByComplexId(complexId).stream()
                .map(AptRentDto::new)
                .toList();
    }

    public List<NearbyComplexDto> getNearbyComplexes(double lat, double lng, double radiusKm) {
        return aptMapper.findNearbyComplexesWithAvgPrice(lat, lng, radiusKm);
    }

    public List<String> getDongsBySigungu(String sigungu) {
        return aptMapper.findDongsBySigungu(sigungu);
    }

    public List<AptComplexSummary> getComplexesForMap(String sigungu, String dong) {
        return aptMapper.findComplexesForMap(sigungu, dong);
    }
}
