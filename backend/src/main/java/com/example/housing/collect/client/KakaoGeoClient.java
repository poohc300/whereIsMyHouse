package com.example.housing.collect.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoGeoClient {

    private static final String GEO_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    private final RestTemplate restTemplate;

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    /**
     * 주소를 위경도 좌표로 변환 (카카오 지오코딩)
     * @return [lat, lng] 배열, 변환 실패 시 null
     */
    @SuppressWarnings("unchecked")
    public double[] geocode(String address) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + restApiKey);

            String url = UriComponentsBuilder.fromHttpUrl(GEO_URL)
                    .queryParam("query", address)
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class
            );

            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");
            if (documents == null || documents.isEmpty()) {
                log.warn("지오코딩 결과 없음: {}", address);
                return null;
            }

            double lng = Double.parseDouble((String) documents.get(0).get("x")); // 경도
            double lat = Double.parseDouble((String) documents.get(0).get("y")); // 위도
            return new double[]{lat, lng};

        } catch (Exception e) {
            log.error("지오코딩 실패 - 주소: {}, 오류: {}", address, e.getMessage());
            return null;
        }
    }
}
