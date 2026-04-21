package com.example.housing.offi.service;

import com.example.housing.offi.mapper.OffiMapper;
import com.example.housing.recommend.dto.AptComplexSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OffiService {

    private final OffiMapper offiMapper;

    public List<AptComplexSummary> getComplexesForMap(String sigungu, String dong) {
        return offiMapper.findComplexesForMap(sigungu, dong);
    }
}
