package com.example.housing.apt.dto;

import com.example.housing.apt.model.AptComplex;
import lombok.Getter;

@Getter
public class AptComplexDto {
    private final Long id;
    private final String name;
    private final String roadAddr;
    private final String sido;
    private final String sigungu;
    private final String dong;
    private final Double lat;
    private final Double lng;
    private final Integer totalHousehold;
    private final Integer buildYear;

    public AptComplexDto(AptComplex c) {
        this.id             = c.getId();
        this.name           = c.getName();
        this.roadAddr       = c.getRoadAddr();
        this.sido           = c.getSido();
        this.sigungu        = c.getSigungu();
        this.dong           = c.getDong();
        this.lat            = c.getLat();
        this.lng            = c.getLng();
        this.totalHousehold = c.getTotalHousehold();
        this.buildYear      = c.getBuildYear();
    }
}
