package com.example.housing.recommend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PriorityScore {
    private int noHousePeriodScore;       // 무주택 기간 점수 (최대 32점)
    private int dependentsScore;          // 부양가족 점수 (최대 35점)
    private int subscriptionPeriodScore;  // 청약통장 기간 점수 (최대 17점)
    private int total;                    // 합계
    private int maxScore;                 // 만점 (84점)
}
