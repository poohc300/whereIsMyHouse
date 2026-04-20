package com.example.housing.apt.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 위경도 기반 반경 조회 결과 DTO.
 * MyBatis가 resultMap을 통해 직접 매핑하므로 @Setter 필요.
 */
@Getter
@Setter
public class NearbyComplexDto {
    private Long    id;
    private String  name;
    private String  roadAddr;
    private String  sigungu;
    private String  dong;
    private Double  lat;
    private Double  lng;
    private Integer totalHousehold;
    private Integer buildYear;
    private Integer avgTradePrice;   // 최근 1년 평균 매매가 (만원), 거래 없으면 null
    private Integer avgDepositPrice; // 최근 1년 평균 전세금 (만원), 거래 없으면 null
    private Double  distanceKm;      // 요청 좌표로부터의 직선 거리 (km)
}
