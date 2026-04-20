package com.example.housing.recommend.dto;

import com.example.housing.subscription.dto.SubscriptionAnnouncementDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendResponse {
    private EligibilityResult eligibility;
    private PriorityScore priorityScore;
    private List<SubscriptionAnnouncementDto> activeSubscriptions;  // 자격에 맞는 현재 청약 공고
    private List<AptComplexSummary> aptRecommendations;             // 예산 내 추천 단지
}
