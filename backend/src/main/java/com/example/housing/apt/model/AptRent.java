package com.example.housing.apt.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AptRent {
    private Long id;
    private Long complexId;
    private String rentType;        // 전세 / 월세
    private Integer dealYear;
    private Integer dealMonth;
    private Double area;            // 전용면적 (㎡)
    private Integer floor;
    private Integer deposit;        // 보증금 (만원)
    private Integer monthlyRent;    // 월세 (만원), 전세 시 null
    private LocalDateTime createdAt;
}
