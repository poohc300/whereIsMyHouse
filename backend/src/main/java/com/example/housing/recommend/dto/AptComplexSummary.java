package com.example.housing.recommend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AptComplexSummary {
    private Long id;
    private String name;
    private String roadAddr;
    private String sigungu;
    private String dong;
    private Double lat;
    private Double lng;
    private Integer avgTradePrice;    // 최근 1년 평균 매매가 (만원), 거래 없으면 null
    private Integer avgDepositPrice;  // 최근 1년 평균 전세금 (만원), 거래 없으면 null
}
