package com.example.housing.user.service;

import com.example.housing.user.dto.UserProfileRequest;
import com.example.housing.user.dto.UserProfileResponse;
import com.example.housing.user.mapper.UserProfileMapper;
import com.example.housing.user.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;

    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            throw new IllegalArgumentException("프로필이 존재하지 않습니다.");
        }
        return new UserProfileResponse(profile);
    }

    public UserProfileResponse saveProfile(Long userId, UserProfileRequest request) {
        UserProfile existing = userProfileMapper.findByUserId(userId);

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setAnnualIncome(request.getAnnualIncome());
        profile.setTotalAssets(request.getTotalAssets());
        profile.setIsHouseOwner(request.getIsHouseOwner());
        profile.setHouseCount(request.getHouseCount());
        profile.setFamilyCount(request.getFamilyCount());
        profile.setIsMarried(request.getIsMarried());
        profile.setSubscriptionPeriod(request.getSubscriptionPeriod());
        profile.setNoHousePeriod(request.getNoHousePeriod());
        profile.setUpdatedAt(LocalDateTime.now());

        if (existing == null) {
            userProfileMapper.insert(profile);
        } else {
            profile.setId(existing.getId());
            userProfileMapper.update(profile);
        }

        return new UserProfileResponse(userProfileMapper.findByUserId(userId));
    }
}
