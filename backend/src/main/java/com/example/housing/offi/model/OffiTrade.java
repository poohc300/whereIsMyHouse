package com.example.housing.offi.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OffiTrade {
    private Long id;
    private Long complexId;
    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;
    private Double area;
    private Integer floor;
    private Integer price;
    private LocalDateTime createdAt;
}
