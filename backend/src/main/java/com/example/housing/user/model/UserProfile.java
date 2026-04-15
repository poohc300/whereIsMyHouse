package com.example.housing.user.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserProfile {
    private Long id;
    private Long userId;
    private Integer annualIncome;       // 연봉 (만원)
    private Integer totalAssets;        // 총 재산 (만원)
    private Boolean isHouseOwner;
    private Integer houseCount;
    private Integer familyCount;
    private Boolean isMarried;
    private Integer subscriptionPeriod; // 청약통장 가입기간 (개월)
    private Integer noHousePeriod;      // 무주택 기간 (개월)
    private LocalDateTime updatedAt;
}
