package com.example.housing.collect.service;

import com.example.housing.collect.client.KakaoGeoClient;
import com.example.housing.collect.client.PublicDataClient;
import com.example.housing.subscription.mapper.SubscriptionMapper;
import com.example.housing.subscription.model.SubscriptionAnnouncement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionCollectService {

    private final PublicDataClient publicDataClient;
    private final KakaoGeoClient kakaoGeoClient;
    private final SubscriptionMapper subscriptionMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 청약 분양공고 전체 수집 (페이지 순회)
     */
    @SuppressWarnings("unchecked")
    public void collect() {
        int page = 1;
        int saved = 0;

        while (true) {
            Map<String, Object> response = publicDataClient.getSubscriptionAnnouncements(page);
            if (response.isEmpty()) break;

            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
            if (data == null || data.isEmpty()) break;

            for (Map<String, Object> item : data) {
                try {
                    String announceNo = String.valueOf(item.get("공고번호"));
                    if (subscriptionMapper.existsByAnnounceNo(announceNo)) continue;

                    SubscriptionAnnouncement announcement = new SubscriptionAnnouncement();
                    announcement.setAnnounceNo(announceNo);
                    announcement.setHouseName(String.valueOf(item.getOrDefault("주택명", "")));
                    announcement.setHouseType(resolveHouseType(String.valueOf(item.getOrDefault("주택구분", ""))));
                    announcement.setSupplyAddr(String.valueOf(item.getOrDefault("공급위치", "")));

                    String receptionStart = String.valueOf(item.getOrDefault("청약접수시작일", ""));
                    String receptionEnd   = String.valueOf(item.getOrDefault("청약접수종료일", ""));
                    String winnerAnnounce = String.valueOf(item.getOrDefault("당첨자발표일", ""));

                    if (!receptionStart.isBlank()) announcement.setReceptionStartDt(parseDate(receptionStart));
                    if (!receptionEnd.isBlank())   announcement.setReceptionEndDt(parseDate(receptionEnd));
                    if (!winnerAnnounce.isBlank())  announcement.setWinnerAnnounceDt(parseDate(winnerAnnounce));

                    String supplyStr = String.valueOf(item.getOrDefault("공급규모", "0"));
                    try { announcement.setTotalSupply(Integer.parseInt(supplyStr.replaceAll("[^\\d]", ""))); }
                    catch (Exception ignored) {}

                    // 카카오 지오코딩
                    String addr = announcement.getSupplyAddr();
                    if (!addr.isBlank()) {
                        double[] coords = kakaoGeoClient.geocode(addr);
                        if (coords != null) {
                            announcement.setLat(coords[0]);
                            announcement.setLng(coords[1]);
                        }
                    }

                    subscriptionMapper.insert(announcement);
                    saved++;
                } catch (Exception e) {
                    log.warn("분양공고 저장 실패: {}", e.getMessage());
                }
            }

            int totalCount = (int) response.getOrDefault("totalCount", 0);
            int perPage    = (int) response.getOrDefault("perPage", 100);
            if ((long) page * perPage >= totalCount) break;
            page++;
        }
        log.info("[청약 수집] 저장={}", saved);
    }

    private String resolveHouseType(String raw) {
        if (raw.contains("민영")) return "민영";
        if (raw.contains("국민")) return "국민";
        return "민영";
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FMT);
        } catch (Exception e) {
            return null;
        }
    }
}
