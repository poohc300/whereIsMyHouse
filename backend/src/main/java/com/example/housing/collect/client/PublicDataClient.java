package com.example.housing.collect.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataClient {

    private static final String APT_TRADE_URL =
            "http://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade";
    private static final String APT_RENT_URL =
            "http://apis.data.go.kr/1613000/RTMSDataSvcAptRent/getRTMSDataSvcAptRent";
    private static final String OFFI_TRADE_URL =
            "http://apis.data.go.kr/1613000/RTMSDataSvcOffiTrade/getRTMSDataSvcOffiTrade";
    private static final String OFFI_RENT_URL =
            "http://apis.data.go.kr/1613000/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent";
    private static final String APT_COMPLEX_URL =
            "http://apis.data.go.kr/1613000/AptListService2/getAptList";
    private static final String OFFICIAL_PRICE_URL =
            "http://apis.data.go.kr/1613000/PublicHouseInfoService/getPubHouseDetail";
    private static final String SUBSCRIPTION_URL =
            "https://api.odcloud.kr/api/ApplyhomeInfoDetailSvc/v1/getAPTLttotPblancDetail";

    private final RestTemplate restTemplate;

    @Value("${public-data.api-key}")
    private String encodedApiKey;

    // .env에 URL 인코딩된 키가 저장되어 있으므로 디코딩 후 사용
    private String getApiKey() {
        return URLDecoder.decode(encodedApiKey, StandardCharsets.UTF_8);
    }

    /**
     * 아파트 매매 실거래가 조회
     * @param lawdCd  법정동코드 앞 5자리 (예: 11680 = 서울 강남구)
     * @param dealYmd 거래년월 (예: 202401)
     */
    public List<Map<String, String>> getAptTrade(String lawdCd, String dealYmd) {
        String url = APT_TRADE_URL
                + "?serviceKey=" + getApiKey()
                + "&LAWD_CD=" + lawdCd
                + "&DEAL_YMD=" + dealYmd
                + "&pageNo=1&numOfRows=1000";
        return fetchXmlItems(url);
    }

    /**
     * 아파트 전월세 실거래가 조회
     * @param lawdCd  법정동코드 앞 5자리
     * @param dealYmd 거래년월 (예: 202401)
     */
    public List<Map<String, String>> getAptRent(String lawdCd, String dealYmd) {
        String url = APT_RENT_URL
                + "?serviceKey=" + getApiKey()
                + "&LAWD_CD=" + lawdCd
                + "&DEAL_YMD=" + dealYmd
                + "&pageNo=1&numOfRows=1000";
        return fetchXmlItems(url);
    }

    public List<Map<String, String>> getOffiTrade(String lawdCd, String dealYmd) {
        String url = OFFI_TRADE_URL
                + "?serviceKey=" + getApiKey()
                + "&LAWD_CD=" + lawdCd
                + "&DEAL_YMD=" + dealYmd
                + "&pageNo=1&numOfRows=1000";
        return fetchXmlItems(url);
    }

    public List<Map<String, String>> getOffiRent(String lawdCd, String dealYmd) {
        String url = OFFI_RENT_URL
                + "?serviceKey=" + getApiKey()
                + "&LAWD_CD=" + lawdCd
                + "&DEAL_YMD=" + dealYmd
                + "&pageNo=1&numOfRows=1000";
        return fetchXmlItems(url);
    }

    /**
     * 공동주택 단지 기본정보 조회
     * @param bjdongCode 법정동코드 앞 5자리
     */
    public List<Map<String, String>> getAptComplexList(String bjdongCode) {
        String url = APT_COMPLEX_URL
                + "?serviceKey=" + getApiKey()
                + "&bjdong_code=" + bjdongCode
                + "&pageNo=1&numOfRows=1000";
        return fetchXmlItems(url);
    }

    /**
     * 공동주택 공시가격 조회
     * @param kaptCode 단지코드
     * @param year     공시년도
     */
    public List<Map<String, String>> getOfficialPrice(String kaptCode, int year) {
        String url = OFFICIAL_PRICE_URL
                + "?serviceKey=" + getApiKey()
                + "&kaptCode=" + kaptCode
                + "&year=" + year
                + "&pageNo=1&numOfRows=1000";
        return fetchXmlItems(url);
    }

    /**
     * 청약 분양공고 조회
     * @param page 페이지 번호 (1부터)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSubscriptionAnnouncements(int page) {
        String url = SUBSCRIPTION_URL
                + "?serviceKey=" + getApiKey()
                + "&page=" + page
                + "&perPage=100";
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("청약 분양공고 조회 실패: {}", e.getMessage());
            return Map.of();
        }
    }

    // XML 응답에서 <item> 목록을 파싱해 Map 리스트로 반환
    private List<Map<String, String>> fetchXmlItems(String url) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            String xml = restTemplate.getForObject(url, String.class);
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                Map<String, String> map = new HashMap<>();
                NodeList children = item.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    if (children.item(j) instanceof Element el) {
                        map.put(el.getTagName(), el.getTextContent().trim());
                    }
                }
                result.add(map);
            }
        } catch (Exception e) {
            log.error("공공 API 호출 실패 - URL: {}, 오류: {}", url, e.getMessage());
        }
        return result;
    }
}
