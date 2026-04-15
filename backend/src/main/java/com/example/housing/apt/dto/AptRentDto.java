package com.example.housing.apt.dto;

import com.example.housing.apt.model.AptRent;
import lombok.Getter;

@Getter
public class AptRentDto {
    private final Long id;
    private final Long complexId;
    private final String rentType;
    private final int dealYear;
    private final int dealMonth;
    private final double area;
    private final int floor;
    private final int deposit;       // 만원
    private final Integer monthlyRent; // 만원, 전세 시 null

    public AptRentDto(AptRent r) {
        this.id          = r.getId();
        this.complexId   = r.getComplexId();
        this.rentType    = r.getRentType();
        this.dealYear    = r.getDealYear();
        this.dealMonth   = r.getDealMonth();
        this.area        = r.getArea();
        this.floor       = r.getFloor();
        this.deposit     = r.getDeposit();
        this.monthlyRent = r.getMonthlyRent();
    }
}
