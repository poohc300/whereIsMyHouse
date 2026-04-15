package com.example.housing.apt.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AptTrade {
    private Long id;
    private Long complexId;
    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;
    private Double area;            // 전용면적 (㎡)
    private Integer floor;
    private Integer price;          // 거래금액 (만원)
    private LocalDateTime createdAt;
}
