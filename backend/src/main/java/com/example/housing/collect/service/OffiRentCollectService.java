package com.example.housing.collect.service;

import com.example.housing.collect.client.PublicDataClient;
import com.example.housing.collect.config.TargetRegions;
import com.example.housing.collect.model.RegionCode;
import com.example.housing.offi.mapper.OffiMapper;
import com.example.housing.offi.model.OffiComplex;
import com.example.housing.offi.model.OffiRent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OffiRentCollectService {

    private final PublicDataClient publicDataClient;
    private final OffiMapper offiMapper;

    public void collect(String lawdCd, String dealYmd) {
        List<Map<String, String>> items = publicDataClient.getOffiRent(lawdCd, dealYmd);
        int saved = 0;

        RegionCode region = TargetRegions.findByLawdCd(lawdCd);
        String sigungu = region != null ? region.sigungu() : "";
        String sido    = region != null ? region.sido()    : "";

        for (Map<String, String> item : items) {
            try {
                String offiNm     = item.get("offiNm");
                String bjdongCode = item.getOrDefault("sggCd", lawdCd);
                String dong       = item.getOrDefault("umdNm", "");
                if (offiNm == null || offiNm.isBlank()) continue;

                OffiComplex complex = findOrCreate(offiNm.trim(), bjdongCode.trim(), sigungu, sido, dong);
                if (complex == null) continue;

                int year    = Integer.parseInt(item.get("dealYear"));
                int month   = Integer.parseInt(item.get("dealMonth"));
                double area = Double.parseDouble(item.get("excluUseAr").trim());
                int floor   = parseIntSafe(item.getOrDefault("floor", "0"));
                int deposit = parsePrice(item.getOrDefault("deposit", "0"));
                int monthlyRent = parsePrice(item.getOrDefault("monthlyRent", "0"));
                String rentType = monthlyRent > 0 ? "월세" : "전세";

                if (offiMapper.existsRent(complex.getId(), year, month, area, floor, deposit)) continue;

                OffiRent rent = new OffiRent();
                rent.setComplexId(complex.getId());
                rent.setRentType(rentType);
                rent.setDealYear(year);
                rent.setDealMonth(month);
                rent.setArea(area);
                rent.setFloor(floor);
                rent.setDeposit(deposit);
                rent.setMonthlyRent(monthlyRent > 0 ? monthlyRent : null);

                offiMapper.insertRent(rent);
                saved++;
            } catch (Exception e) {
                log.warn("오피스텔 전월세 저장 실패: {}", e.getMessage());
            }
        }
        log.info("[오피스텔 전월세 수집] lawd={}, ymd={}, 수집={}, 저장={}", lawdCd, dealYmd, items.size(), saved);
    }

    private OffiComplex findOrCreate(String name, String bjdongCode, String sigungu, String sido, String dong) {
        OffiComplex complex = offiMapper.findComplexByNameAndBjdong(name, bjdongCode);
        if (complex != null) return complex;

        complex = new OffiComplex();
        complex.setName(name);
        complex.setBjdongCode(bjdongCode);
        complex.setSigungu(sigungu);
        complex.setSido(sido);
        complex.setDong(dong);
        offiMapper.insertComplex(complex);
        return complex.getId() != null ? complex : offiMapper.findComplexByNameAndBjdong(name, bjdongCode);
    }

    private int parsePrice(String priceStr) {
        if (priceStr == null) return 0;
        return Integer.parseInt(priceStr.replaceAll("[,\\s]", ""));
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}
