package com.example.housing.recommend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EligibilityResult {
    private boolean nationalHousing;     // 국민주택 신청 가능
    private boolean privateHousing;      // 민영주택 신청 가능
    private boolean specialNewlywed;     // 신혼부부 특별공급 해당
    private boolean specialFirstTime;    // 생애최초 특별공급 해당 (추정)
    private boolean specialMultiChild;   // 다자녀 특별공급 해당
}
