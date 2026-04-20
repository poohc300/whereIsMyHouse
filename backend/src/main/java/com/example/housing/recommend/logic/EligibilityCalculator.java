package com.example.housing.recommend.logic;

import com.example.housing.recommend.dto.*;
import org.springframework.stereotype.Component;

/**
 * 사용자 설문 응답을 바탕으로 청약 자격 여부를 추정한다.
 * DB에 어떠한 데이터도 저장하지 않는다.
 */
@Component
public class EligibilityCalculator {

    public EligibilityResult calculate(RecommendRequest req) {
        boolean noHouse  = req.getHouseOwnership() == HouseOwnership.NONE;
        boolean oneHouse = req.getHouseOwnership() == HouseOwnership.ONE;

        // 국민주택: 무주택 + 소득 7천만원 이하 + 총자산 3억 이하 (대략 기준)
        boolean nationalHousing = noHouse
                && req.getAnnualIncomeRange().ordinal() <= AnnualIncomeRange.RANGE_50M_70M.ordinal()
                && req.getTotalAssetsRange().ordinal() <= TotalAssetsRange.RANGE_100M_300M.ordinal();

        // 민영주택: 무주택 또는 1주택 (일반공급 기준)
        boolean privateHousing = noHouse || oneHouse;

        // 신혼부부 특별공급: 결혼 7년 이내 + 무주택 + 소득 1억 이하
        boolean specialNewlywed = req.getMaritalStatus() == MaritalStatus.MARRIED_WITHIN_7Y
                && noHouse
                && req.getAnnualIncomeRange().ordinal() <= AnnualIncomeRange.RANGE_70M_100M.ordinal();

        // 생애최초 특별공급: 무주택 + 30대 이상 (세금 납부 이력을 연령으로 추정)
        boolean specialFirstTime = noHouse
                && req.getAgeGroup().ordinal() >= AgeGroup.THIRTIES.ordinal();

        // 다자녀 특별공급: 무주택 + 자녀 3명 이상
        boolean specialMultiChild = noHouse && req.getChildCount() >= 3;

        return EligibilityResult.builder()
                .nationalHousing(nationalHousing)
                .privateHousing(privateHousing)
                .specialNewlywed(specialNewlywed)
                .specialFirstTime(specialFirstTime)
                .specialMultiChild(specialMultiChild)
                .build();
    }
}
