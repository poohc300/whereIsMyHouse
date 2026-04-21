package com.example.housing.collect.controller;

import com.example.housing.apt.mapper.AptMapper;
import com.example.housing.apt.model.AptComplex;
import com.example.housing.collect.client.KakaoGeoClient;
import com.example.housing.collect.config.TargetRegions;
import com.example.housing.collect.scheduler.CollectScheduler;
import com.example.housing.collect.service.AptComplexCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/collect")
@RequiredArgsConstructor
public class CollectController {

    private final CollectScheduler collectScheduler;
    private final KakaoGeoClient kakaoGeoClient;
    private final AptMapper aptMapper;

    /**
     * 최근 N개월치 실거래가 수동 수집 트리거.
     * 수집은 백그라운드 스레드에서 실행되므로 응답은 즉시 반환된다.
     *
     * POST /api/admin/collect?months=3
     *
     * @param months 수집할 개월 수 (기본값 3, 최대 24)
     */
    @PostMapping
    public ResponseEntity<String> triggerCollect(
            @RequestParam(defaultValue = "3") int months) {

        int safeMonths = Math.min(months, 24);
        log.info("[수동 수집 요청] months={}", safeMonths);

        // 오래 걸리는 작업이므로 별도 스레드에서 실행 후 즉시 202 반환
        int finalSafeMonths = safeMonths;
        Thread.ofVirtual()
              .name("collect-manual")
              .start(() -> collectScheduler.collectRecentMonths(finalSafeMonths));

        return ResponseEntity.accepted()
                .body("수집 시작됨. 최근 " + safeMonths + "개월 / 백그라운드에서 진행 중입니다.");
    }

    /**
     * 좌표 없는 단지를 카카오 지오코딩으로 보정.
     * POST /api/admin/collect/geocode
     */
    @PostMapping("/sido")
    public ResponseEntity<String> triggerCollectBySido(
            @RequestParam String sido,
            @RequestParam(defaultValue = "60") int months) {

        int safeMonths = Math.min(months, 60);
        log.info("[시도별 수집 요청] sido={}, months={}", sido, safeMonths);

        Thread.ofVirtual()
              .name("collect-sido-" + sido)
              .start(() -> collectScheduler.collectBySido(sido, safeMonths));

        return ResponseEntity.accepted()
                .body(sido + " 수집 시작됨. 최근 " + safeMonths + "개월 / 백그라운드에서 진행 중입니다.");
    }

    @PostMapping("/geocode")
    public ResponseEntity<String> triggerGeocode() {
        Thread.ofVirtual()
              .name("geocode-repair")
              .start(() -> {
                  var complexes = aptMapper.findComplexesWithoutCoords();
                  log.info("[지오코딩 보정] 대상 단지 수: {}", complexes.size());
                  int fixed = 0;
                  for (AptComplex c : complexes) {
                      String query = (c.getSigungu() != null && !c.getSigungu().isBlank())
                              ? c.getSigungu() + " " + c.getName()
                              : c.getName();
                      double[] coords = kakaoGeoClient.geocode(query);
                      if (coords != null) {
                          aptMapper.updateComplexCoords(c.getId(), coords[0], coords[1]);
                          fixed++;
                      }
                      try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                  }
                  log.info("[지오코딩 보정] 완료 - {}/{} 좌표 획득", fixed, complexes.size());
              });
        return ResponseEntity.accepted().body("지오코딩 보정 시작됨. 백그라운드에서 진행 중입니다.");
    }
}
