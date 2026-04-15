package com.example.housing.apt.dto;

import com.example.housing.apt.model.AptTrade;
import lombok.Getter;

@Getter
public class AptTradeDto {
    private final Long id;
    private final Long complexId;
    private final int dealYear;
    private final int dealMonth;
    private final int dealDay;
    private final double area;
    private final int floor;
    private final int price;        // 만원

    public AptTradeDto(AptTrade t) {
        this.id        = t.getId();
        this.complexId = t.getComplexId();
        this.dealYear  = t.getDealYear();
        this.dealMonth = t.getDealMonth();
        this.dealDay   = t.getDealDay();
        this.area      = t.getArea();
        this.floor     = t.getFloor();
        this.price     = t.getPrice();
    }
}
