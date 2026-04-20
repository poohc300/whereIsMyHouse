package com.example.housing.recommend.dto;

import lombok.Getter;

@Getter
public class RecommendRequest {
    private AgeGroup ageGroup;
    private MaritalStatus maritalStatus;
    private int childCount;                  // 자녀 수
    private HouseOwnership houseOwnership;
    private AnnualIncomeRange annualIncomeRange;
    private TotalAssetsRange totalAssetsRange;
    private SubscriptionPeriod subscriptionPeriod;
    private String preferredSigungu;         // 선호 시군구 (예: "마포구")
}
