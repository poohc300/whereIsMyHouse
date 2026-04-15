package com.example.housing.apt.service;

import com.example.housing.apt.dto.AptComplexDto;
import com.example.housing.apt.dto.AptRentDto;
import com.example.housing.apt.dto.AptTradeDto;
import com.example.housing.apt.mapper.AptMapper;
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
}
