package com.example.housing.offi.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OffiRent {
    private Long id;
    private Long complexId;
    private String rentType;
    private Integer dealYear;
    private Integer dealMonth;
    private Double area;
    private Integer floor;
    private Integer deposit;
    private Integer monthlyRent;
    private LocalDateTime createdAt;
}
