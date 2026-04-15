package com.example.housing.collect.service;

import com.example.housing.apt.mapper.AptMapper;
import com.example.housing.apt.model.AptComplex;
import com.example.housing.apt.model.AptRent;
import com.example.housing.collect.client.PublicDataClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AptRentCollectService {

    private final PublicDataClient publicDataClient;
    private final AptComplexCollectService complexCollectService;
    private final AptMapper aptMapper;

    /**
     * 특정 지역·월의 아파트 전월세 실거래가 수집
     * @param lawdCd  법정동코드 앞 5자리
     * @param dealYmd 거래년월 (예: 202401)
     */
    public void collect(String lawdCd, String dealYmd) {
        List<Map<String, String>> items = publicDataClient.getAptRent(lawdCd, dealYmd);
        int saved = 0;

        for (Map<String, String> item : items) {
            try {
                String aptName = item.get("아파트");
                String bjdongCode = item.getOrDefault("지역코드", lawdCd);
                if (aptName == null) continue;

                AptComplex complex = complexCollectService.findOrCreate(aptName, bjdongCode);
                if (complex == null) continue;

                int year  = Integer.parseInt(item.get("년"));
                int month = Integer.parseInt(item.get("월"));
                double area = Double.parseDouble(item.get("전용면적").trim());
                int floor = parseIntSafe(item.getOrDefault("층", "0"));
                int deposit = parsePrice(item.getOrDefault("보증금액", "0"));
                int monthlyRent = parsePrice(item.getOrDefault("월세금액", "0"));
                String rentType = monthlyRent > 0 ? "월세" : "전세";

                if (aptMapper.existsRent(complex.getId(), year, month, area, floor, deposit)) continue;

                AptRent rent = new AptRent();
                rent.setComplexId(complex.getId());
                rent.setRentType(rentType);
                rent.setDealYear(year);
                rent.setDealMonth(month);
                rent.setArea(area);
                rent.setFloor(floor);
                rent.setDeposit(deposit);
                rent.setMonthlyRent(monthlyRent > 0 ? monthlyRent : null);

                aptMapper.insertRent(rent);
                saved++;
            } catch (Exception e) {
                log.warn("전월세 실거래가 저장 실패: {}", e.getMessage());
            }
        }
        log.info("[전월세 수집] lawd={}, ymd={}, 수집={}, 저장={}", lawdCd, dealYmd, items.size(), saved);
    }

    private int parsePrice(String priceStr) {
        if (priceStr == null) return 0;
        return Integer.parseInt(priceStr.replaceAll("[,\\s]", ""));
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}
