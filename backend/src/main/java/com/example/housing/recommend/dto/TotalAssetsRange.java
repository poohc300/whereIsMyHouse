package com.example.housing.recommend.dto;

public enum TotalAssetsRange {
    // (maxTradeBudget, maxDepositBudget) 단위: 만원
    UNDER_100M      (10_000,  8_000),   // 자산 1억 미만
    RANGE_100M_300M (30_000, 24_000),   // 자산 1억 ~ 3억
    RANGE_300M_500M (50_000, 40_000),   // 자산 3억 ~ 5억
    OVER_500M       (Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2); // 자산 5억 초과

    private final int maxTradeBudget;
    private final int maxDepositBudget;

    TotalAssetsRange(int maxTradeBudget, int maxDepositBudget) {
        this.maxTradeBudget = maxTradeBudget;
        this.maxDepositBudget = maxDepositBudget;
    }

    public int getMaxTradeBudget() { return maxTradeBudget; }
    public int getMaxDepositBudget() { return maxDepositBudget; }
}
