package com.example.housing.apt.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AptComplex {
    private Long id;
    private String complexNo;       // API 단지코드
    private String name;
    private String roadAddr;
    private String sido;
    private String sigungu;
    private String dong;
    private String bjdongCode;      // 법정동코드 앞 5자리
    private Double lat;
    private Double lng;
    private Integer totalHousehold;
    private Integer buildYear;
    private LocalDateTime createdAt;
}
