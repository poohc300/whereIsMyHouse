package com.example.housing.apt.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AptOfficialPrice {
    private Long id;
    private Long complexId;
    private Integer year;           // 공시년도
    private String dong;
    private String ho;
    private Double area;            // 전용면적 (㎡)
    private Long officialPrice;     // 공시가격 (원)
    private LocalDateTime createdAt;
}
