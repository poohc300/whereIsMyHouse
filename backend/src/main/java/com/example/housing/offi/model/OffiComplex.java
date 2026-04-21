package com.example.housing.offi.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OffiComplex {
    private Long id;
    private String name;
    private String sido;
    private String sigungu;
    private String dong;
    private String bjdongCode;
    private Double lat;
    private Double lng;
    private LocalDateTime createdAt;
}
