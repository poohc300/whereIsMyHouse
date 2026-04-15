package com.example.housing.subscription.service;

import com.example.housing.subscription.dto.SubscriptionAnnouncementDto;
import com.example.housing.subscription.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;

    public List<SubscriptionAnnouncementDto> getAll() {
        return subscriptionMapper.findAll().stream()
                .map(SubscriptionAnnouncementDto::new)
                .toList();
    }

    public List<SubscriptionAnnouncementDto> getByDong(String bjdongCode) {
        return subscriptionMapper.findByBjdong(bjdongCode).stream()
                .map(SubscriptionAnnouncementDto::new)
                .toList();
    }

    public List<SubscriptionAnnouncementDto> getActive() {
        return subscriptionMapper.findActive().stream()
                .map(SubscriptionAnnouncementDto::new)
                .toList();
    }
}
