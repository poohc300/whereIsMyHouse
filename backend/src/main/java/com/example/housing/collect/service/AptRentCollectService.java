package com.example.housing.collect.service;

import com.example.housing.apt.mapper.AptMapper;
import com.example.housing.apt.model.AptComplex;
import com.example.housing.apt.model.AptRent;
import com.example.housing.collect.client.PublicDataClient;
import com.example.housing.collect.config.TargetRegions;
import com.example.housing.collect.model.RegionCode;
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

        // lawdCd로 시도/시군구 정보를 조회해 complex 생성 시 설정
        RegionCode region = TargetRegions.findByLawdCd(lawdCd);
        String sigungu = region != null ? region.sigungu() : "";
        String sido    = region != null ? region.sido()    : "";

        for (Map<String, String> item : items) {
            try {
                // 공공 API 필드명 (영문 camelCase 버전)
                String aptName    = item.get("aptNm");
                String bjdongCode = item.getOrDefault("sggCd", lawdCd);
                if (aptName == null || aptName.isBlank()) continue;

                AptComplex complex = complexCollectService.findOrCreate(aptName.trim(), bjdongCode.trim(), sigungu, sido);
                if (complex == null) continue;

                int year    = Integer.parseInt(item.get("dealYear"));
                int month   = Integer.parseInt(item.get("dealMonth"));
                double area = Double.parseDouble(item.get("excluUseAr").trim());
                int floor   = parseIntSafe(item.getOrDefault("floor", "0"));
                int deposit = parsePrice(item.getOrDefault("deposit", "0"));
                int monthlyRent = parsePrice(item.getOrDefault("monthlyRent", "0"));
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
