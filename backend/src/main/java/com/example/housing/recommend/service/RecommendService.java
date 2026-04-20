package com.example.housing.recommend.service;

import com.example.housing.apt.mapper.AptMapper;
import com.example.housing.recommend.dto.*;
import com.example.housing.recommend.logic.EligibilityCalculator;
import com.example.housing.recommend.logic.PriorityScoreCalculator;
import com.example.housing.subscription.dto.SubscriptionAnnouncementDto;
import com.example.housing.subscription.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 설문 응답을 받아 자격 판단 + 추천 결과를 반환한다.
 * 사용자 입력을 DB에 일절 저장하지 않는다 (Stateless).
 */
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final EligibilityCalculator eligibilityCalculator;
    private final PriorityScoreCalculator priorityScoreCalculator;
    private final SubscriptionMapper subscriptionMapper;
    private final AptMapper aptMapper;

    public RecommendResponse recommend(RecommendRequest req) {
        EligibilityResult eligibility = eligibilityCalculator.calculate(req);
        PriorityScore priorityScore   = priorityScoreCalculator.calculate(req);

        List<SubscriptionAnnouncementDto> subscriptions = filterSubscriptions(req, eligibility);
        List<AptComplexSummary> aptRecommendations      = filterApts(req);

        return RecommendResponse.builder()
                .eligibility(eligibility)
                .priorityScore(priorityScore)
                .activeSubscriptions(subscriptions)
                .aptRecommendations(aptRecommendations)
                .build();
    }

    /**
     * 현재 접수 중인 청약 공고 중 자격 조건 + 선호 지역에 맞는 것만 반환
     */
    private List<SubscriptionAnnouncementDto> filterSubscriptions(RecommendRequest req,
                                                                   EligibilityResult eligibility) {
        return subscriptionMapper.findActive().stream()
                .filter(s -> {
                    if ("국민".equals(s.getHouseType()) && !eligibility.isNationalHousing()) return false;
                    if ("민영".equals(s.getHouseType()) && !eligibility.isPrivateHousing()) return false;
                    return true;
                })
                .filter(s -> {
                    if (req.getPreferredSigungu() == null || req.getPreferredSigungu().isBlank()) return true;
                    return s.getSupplyAddr() != null
                            && s.getSupplyAddr().contains(req.getPreferredSigungu());
                })
                .map(SubscriptionAnnouncementDto::new)
                .toList();
    }

    /**
     * 선호 시군구 내 단지 중 예산 범위에 맞는 단지 최대 10개 반환
     */
    private List<AptComplexSummary> filterApts(RecommendRequest req) {
        if (req.getPreferredSigungu() == null || req.getPreferredSigungu().isBlank()) {
            return List.of();
        }

        int maxTrade   = req.getTotalAssetsRange().getMaxTradeBudget();
        int maxDeposit = req.getTotalAssetsRange().getMaxDepositBudget();

        return aptMapper.findComplexesWithAvgPriceBySigungu(req.getPreferredSigungu()).stream()
                .filter(c -> {
                    boolean tradeOk   = c.getAvgTradePrice() == null   || c.getAvgTradePrice() <= maxTrade;
                    boolean depositOk = c.getAvgDepositPrice() == null || c.getAvgDepositPrice() <= maxDeposit;
                    return tradeOk || depositOk;
                })
                .limit(10)
                .toList();
    }
}
