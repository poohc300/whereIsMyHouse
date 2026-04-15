package com.example.housing.collect.service;

import com.example.housing.apt.mapper.AptMapper;
import com.example.housing.apt.model.AptComplex;
import com.example.housing.collect.client.KakaoGeoClient;
import com.example.housing.collect.client.PublicDataClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AptComplexCollectService {

    private final PublicDataClient publicDataClient;
    private final KakaoGeoClient kakaoGeoClient;
    private final AptMapper aptMapper;

    /**
     * 특정 법정동의 아파트 단지 기본정보 수집
     * @param bjdongCode 법정동코드 앞 5자리
     */
    public void collectByDong(String bjdongCode) {
        List<Map<String, String>> items = publicDataClient.getAptComplexList(bjdongCode);
        int saved = 0;

        for (Map<String, String> item : items) {
            String complexNo = item.get("kaptCode");
            if (complexNo == null) continue;

            if (aptMapper.findComplexByComplexNo(complexNo) != null) continue;

            AptComplex complex = new AptComplex();
            complex.setComplexNo(complexNo);
            complex.setName(item.getOrDefault("kaptName", ""));
            complex.setRoadAddr(item.getOrDefault("doroJuso", ""));
            complex.setBjdongCode(bjdongCode);
            complex.setSido(item.getOrDefault("sidoNm", ""));
            complex.setSigungu(item.getOrDefault("sggNm", ""));
            complex.setDong(item.getOrDefault("emdNm", ""));

            String buildYear = item.get("kaptBuildYe");
            if (buildYear != null && !buildYear.isBlank()) {
                try { complex.setBuildYear(Integer.parseInt(buildYear)); } catch (NumberFormatException ignored) {}
            }
            String household = item.get("kaptdaCnt");
            if (household != null && !household.isBlank()) {
                try { complex.setTotalHousehold(Integer.parseInt(household)); } catch (NumberFormatException ignored) {}
            }

            // 카카오 지오코딩으로 좌표 설정
            String addr = complex.getRoadAddr().isBlank() ? complex.getName() : complex.getRoadAddr();
            double[] coords = kakaoGeoClient.geocode(addr);
            if (coords != null) {
                complex.setLat(coords[0]);
                complex.setLng(coords[1]);
            }

            aptMapper.insertComplex(complex);
            saved++;
        }
        log.info("[단지 수집] bjdong={}, 수집={}, 저장={}", bjdongCode, items.size(), saved);
    }

    /**
     * 주소로 단지를 찾고 없으면 최소 정보로 생성 (실거래가 수집 시 사용)
     */
    public AptComplex findOrCreate(String name, String bjdongCode) {
        AptComplex existing = aptMapper.findComplexByNameAndBjdong(name, bjdongCode);
        if (existing != null) return existing;

        AptComplex complex = new AptComplex();
        complex.setComplexNo("UNKNOWN_" + bjdongCode + "_" + name.replaceAll("\\s", ""));
        complex.setName(name);
        complex.setBjdongCode(bjdongCode);

        double[] coords = kakaoGeoClient.geocode(name);
        if (coords != null) {
            complex.setLat(coords[0]);
            complex.setLng(coords[1]);
        }

        aptMapper.insertComplex(complex);
        return aptMapper.findComplexByNameAndBjdong(name, bjdongCode);
    }
}
