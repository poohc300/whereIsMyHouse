package com.example.housing.subscription.dto;

import com.example.housing.subscription.model.SubscriptionAnnouncement;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SubscriptionAnnouncementDto {
    private final Long id;
    private final String announceNo;
    private final String houseName;
    private final String houseType;
    private final String supplyAddr;
    private final Double lat;
    private final Double lng;
    private final Integer totalSupply;
    private final LocalDate receptionStartDt;
    private final LocalDate receptionEndDt;
    private final LocalDate winnerAnnounceDt;
    private final Integer minPrice;
    private final Integer maxPrice;

    public SubscriptionAnnouncementDto(SubscriptionAnnouncement s) {
        this.id                = s.getId();
        this.announceNo        = s.getAnnounceNo();
        this.houseName         = s.getHouseName();
        this.houseType         = s.getHouseType();
        this.supplyAddr        = s.getSupplyAddr();
        this.lat               = s.getLat();
        this.lng               = s.getLng();
        this.totalSupply       = s.getTotalSupply();
        this.receptionStartDt  = s.getReceptionStartDt();
        this.receptionEndDt    = s.getReceptionEndDt();
        this.winnerAnnounceDt  = s.getWinnerAnnounceDt();
        this.minPrice          = s.getMinPrice();
        this.maxPrice          = s.getMaxPrice();
    }
}
