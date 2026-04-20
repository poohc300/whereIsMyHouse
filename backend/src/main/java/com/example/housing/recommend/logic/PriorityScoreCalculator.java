package com.example.housing.recommend.logic;

import com.example.housing.recommend.dto.*;
import org.springframework.stereotype.Component;

/**
 * 민영주택 청약 가점을 추정한다. (84점 만점)
 *  - 무주택 기간: 최대 32점
 *  - 부양가족 수: 최대 35점
 *  - 청약통장 기간: 최대 17점
 *
 * 정확한 기간 대신 연령대·선택지로 근삿값을 산출한다.
 */
@Component
public class PriorityScoreCalculator {

    private static final int MAX_SCORE = 84;

    public PriorityScore calculate(RecommendRequest req) {
        int noHouseScore      = calcNoHousePeriodScore(req);
        int dependentsScore   = calcDependentsScore(req);
        int subscriptionScore = calcSubscriptionPeriodScore(req.getSubscriptionPeriod());

        return PriorityScore.builder()
                .noHousePeriodScore(noHouseScore)
                .dependentsScore(dependentsScore)
                .subscriptionPeriodScore(subscriptionScore)
                .total(noHouseScore + dependentsScore + subscriptionScore)
                .maxScore(MAX_SCORE)
                .build();
    }

    /**
     * 무주택 기간 점수: 연령대를 기반으로 추정
     * (실제 가점제: 1년마다 2점씩, 최대 32점)
     */
    private int calcNoHousePeriodScore(RecommendRequest req) {
        if (req.getHouseOwnership() != HouseOwnership.NONE) return 0;
        return switch (req.getAgeGroup()) {
            case TWENTIES    -> 4;   // 약 2년 추정
            case THIRTIES    -> 16;  // 약 8년 추정
            case FORTIES     -> 24;  // 약 12년 추정
            case OVER_FIFTIES -> 32; // 15년 이상 추정
        };
    }

    /**
     * 부양가족 점수: 배우자 + 자녀 수로 산정
     * (실제 가점제: 0명=5점, 1명=10점 ... 6명 이상=35점)
     */
    private int calcDependentsScore(RecommendRequest req) {
        int spouse = (req.getMaritalStatus() != MaritalStatus.SINGLE) ? 1 : 0;
        int total  = spouse + req.getChildCount();
        return switch (total) {
            case 0  ->  5;
            case 1  -> 10;
            case 2  -> 15;
            case 3  -> 20;
            case 4  -> 25;
            case 5  -> 30;
            default -> 35;
        };
    }

    /**
     * 청약통장 기간 점수
     * (실제 가점제: 6개월 미만=1점 ~ 15년 이상=17점)
     */
    private int calcSubscriptionPeriodScore(SubscriptionPeriod period) {
        return switch (period) {
            case NONE        ->  0;
            case UNDER_1Y    ->  1;
            case RANGE_1Y_2Y ->  3;
            case RANGE_2Y_5Y ->  6;
            case RANGE_5Y_10Y -> 10;
            case OVER_10Y    -> 15;
        };
    }
}
