package com.example.housing.subscription.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubscriptionAnnouncement {
    private Long id;
    private String announceNo;          // 공고번호
    private String houseName;
    private String houseType;           // 민영 / 국민
    private String supplyAddr;
    private String bjdongCode;
    private Double lat;
    private Double lng;
    private Integer totalSupply;
    private LocalDate receptionStartDt;
    private LocalDate receptionEndDt;
    private LocalDate winnerAnnounceDt;
    private Integer minPrice;           // 최저 분양가 (만원)
    private Integer maxPrice;           // 최고 분양가 (만원)
    private LocalDateTime createdAt;
}
