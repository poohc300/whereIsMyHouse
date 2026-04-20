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
     * 단지를 이름+법정동 기준으로 찾고, 없으면 최소 정보로 생성한다 (실거래가 수집 시 사용).
     * sigungu / sido 를 함께 전달하면 지역 필터 쿼리에서 올바르게 조회된다.
     */
    public AptComplex findOrCreate(String name, String bjdongCode, String sigungu, String sido) {
        AptComplex existing = aptMapper.findComplexByNameAndBjdong(name, bjdongCode);
        if (existing != null) {
            // 이전 수집에서 sigungu 없이 저장된 경우 자동 보정
            if ((existing.getSigungu() == null || existing.getSigungu().isBlank()) && !sigungu.isBlank()) {
                aptMapper.updateComplexSigunguAndSido(existing.getId(), sigungu, sido);
                existing.setSigungu(sigungu);
                existing.setSido(sido);
            }
            return existing;
        }

        AptComplex complex = new AptComplex();
        complex.setComplexNo("UNKNOWN_" + bjdongCode + "_" + name.replaceAll("\\s", ""));
        complex.setName(name);
        complex.setBjdongCode(bjdongCode);
        complex.setSigungu(sigungu != null ? sigungu : "");
        complex.setSido(sido != null ? sido : "");

        // 시군구 포함 검색어 사용 시 정확도가 높아짐 (예: "마포구 래미안푸르지오")
        String geocodeQuery = (sigungu != null && !sigungu.isBlank())
                ? sigungu + " " + name
                : name;
        double[] coords = kakaoGeoClient.geocode(geocodeQuery);
        if (coords != null) {
            complex.setLat(coords[0]);
            complex.setLng(coords[1]);
        }

        aptMapper.insertComplex(complex);
        return aptMapper.findComplexByNameAndBjdong(name, bjdongCode);
    }
}
