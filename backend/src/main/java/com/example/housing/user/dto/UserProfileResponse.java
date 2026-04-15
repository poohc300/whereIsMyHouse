package com.example.housing.user.dto;

import com.example.housing.user.model.UserProfile;
import lombok.Getter;

@Getter
public class UserProfileResponse {
    private final Integer annualIncome;
    private final Integer totalAssets;
    private final Boolean isHouseOwner;
    private final Integer houseCount;
    private final Integer familyCount;
    private final Boolean isMarried;
    private final Integer subscriptionPeriod;
    private final Integer noHousePeriod;

    public UserProfileResponse(UserProfile p) {
        this.annualIncome       = p.getAnnualIncome();
        this.totalAssets        = p.getTotalAssets();
        this.isHouseOwner       = p.getIsHouseOwner();
        this.houseCount         = p.getHouseCount();
        this.familyCount        = p.getFamilyCount();
        this.isMarried          = p.getIsMarried();
        this.subscriptionPeriod = p.getSubscriptionPeriod();
        this.noHousePeriod      = p.getNoHousePeriod();
    }
}
