package com.example.housing.collect.service;

import com.example.housing.apt.mapper.AptMapper;
import com.example.housing.apt.model.AptComplex;
import com.example.housing.apt.model.AptOfficialPrice;
import com.example.housing.collect.client.PublicDataClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfficialPriceCollectService {

    private final PublicDataClient publicDataClient;
    private final AptMapper aptMapper;

    /**
     * 단지별 공동주택 공시가격 수집
     * @param bjdongCode 법정동코드 앞 5자리
     * @param year       공시년도
     */
    public void collectByDong(String bjdongCode, int year) {
        List<AptComplex> complexes = aptMapper.findComplexesByBjdong(bjdongCode);
        int saved = 0;

        for (AptComplex complex : complexes) {
            List<Map<String, String>> items = publicDataClient.getOfficialPrice(complex.getComplexNo(), year);

            for (Map<String, String> item : items) {
                try {
                    if (!aptMapper.findOfficialPricesByComplexAndYear(complex.getId(), year).isEmpty()) continue;

                    AptOfficialPrice price = new AptOfficialPrice();
                    price.setComplexId(complex.getId());
                    price.setYear(year);
                    price.setDong(item.getOrDefault("동명", ""));
                    price.setHo(item.getOrDefault("호명", ""));

                    String areaStr = item.get("전용면적");
                    if (areaStr != null && !areaStr.isBlank()) {
                        price.setArea(Double.parseDouble(areaStr.trim()));
                    }
                    String priceStr = item.get("공시가격");
                    if (priceStr != null && !priceStr.isBlank()) {
                        price.setOfficialPrice(Long.parseLong(priceStr.replaceAll("[,\\s]", "")));
                    }

                    aptMapper.insertOfficialPrice(price);
                    saved++;
                } catch (Exception e) {
                    log.warn("공시가격 저장 실패: {}", e.getMessage());
                }
            }
        }
        log.info("[공시가격 수집] bjdong={}, year={}, 저장={}", bjdongCode, year, saved);
    }
}
