package com.example.housing.collect.scheduler;

import com.example.housing.collect.config.TargetRegions;
import com.example.housing.collect.model.RegionCode;
import com.example.housing.collect.service.AptRentCollectService;
import com.example.housing.collect.service.AptTradeCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectScheduler {

    private final AptTradeCollectService tradeCollectService;
    private final AptRentCollectService  rentCollectService;

    // 거래년월 포맷 (공공API 규격: yyyyMM)
    private static final DateTimeFormatter YMD_FMT = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 매월 1일 새벽 3시: 전월 실거래가 수집.
     * 예) 5월 1일 실행 → 4월(202504) 데이터 수집
     */
    @Scheduled(cron = "0 0 3 1 * *")
    public void collectPreviousMonth() {
        String dealYmd = YearMonth.now().minusMonths(1).format(YMD_FMT);
        log.info("[스케줄] 전월 수집 시작 - dealYmd={}, 대상 지역={}", dealYmd, TargetRegions.ALL.size());
        collectForMonths(List.of(dealYmd));
    }

    /**
     * 최근 N개월치 데이터를 전 지역에 대해 수집한다.
     * 이미 저장된 데이터는 existsTrade / existsRent 중복 체크로 자동 건너뜀.
     *
     * @param months 수집할 개월 수 (1 = 이번 달, 2 = 이번 달 + 전월, ...)
     */
    public void collectRecentMonths(int months) {
        List<String> targets = buildMonthList(months);
        log.info("[수동 수집] 시작 - months={}, 대상 지역={}, 대상 월={}",
                months, TargetRegions.ALL.size(), targets);
        collectForMonths(targets);
        log.info("[수동 수집] 완료 - months={}", months);
    }

    public void collectBySido(String sido, int months) {
        List<RegionCode> regions = TargetRegions.ALL.stream()
                .filter(r -> r.sido().equals(sido))
                .toList();
        List<String> targets = buildMonthList(months);
        log.info("[시도별 수집] 시작 - sido={}, regions={}, months={}", sido, regions.size(), months);
        collectForRegions(regions, targets);
        log.info("[시도별 수집] 완료 - sido={}", sido);
    }

    // ─────────────────────────────────────────────────────────────────

    /**
     * 지정한 거래년월 목록에 대해 전 지역 매매+전월세를 순차 수집한다.
     * 공공 API 과호출 방지를 위해 지역마다 200ms 딜레이를 둔다.
     */
    private void collectForMonths(List<String> dealYmds) {
        collectForRegions(TargetRegions.ALL, dealYmds);
    }

    private void collectForRegions(List<RegionCode> regions, List<String> dealYmds) {
        int total = regions.size();
        int idx = 0;

        for (RegionCode region : regions) {
            idx++;
            for (String dealYmd : dealYmds) {
                try {
                    tradeCollectService.collect(region.lawdCd(), dealYmd);
                    rentCollectService.collect(region.lawdCd(), dealYmd);
                } catch (Exception e) {
                    log.error("[수집 오류] region={}/{}, dealYmd={}: {}",
                            region.sido(), region.sigungu(), dealYmd, e.getMessage());
                }

                sleepMillis(200);
            }

            if (idx % 10 == 0) {
                log.info("[수집 진행] {}/{} 지역 완료", idx, total);
            }
        }
    }

    /**
     * 현재 달로부터 과거 N개월의 거래년월 문자열 목록을 반환한다.
     * 예) months=3, 현재 2026-04 → ["202604", "202603", "202602"]
     */
    private List<String> buildMonthList(int months) {
        YearMonth base = YearMonth.now();
        return java.util.stream.IntStream.range(0, months)
                .mapToObj(i -> base.minusMonths(i).format(YMD_FMT))
                .toList();
    }

    private void sleepMillis(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
