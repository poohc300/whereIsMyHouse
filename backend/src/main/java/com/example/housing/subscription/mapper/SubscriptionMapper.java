package com.example.housing.subscription.mapper;

import com.example.housing.subscription.model.SubscriptionAnnouncement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubscriptionMapper {

    List<SubscriptionAnnouncement> findAll();
    List<SubscriptionAnnouncement> findByBjdong(String bjdongCode);
    List<SubscriptionAnnouncement> findActive(); // 현재 청약 접수 중
    SubscriptionAnnouncement findByAnnounceNo(String announceNo);
    void insert(SubscriptionAnnouncement announcement);
    void updateCoords(@Param("id") Long id, @Param("lat") Double lat, @Param("lng") Double lng);
    boolean existsByAnnounceNo(String announceNo);
}
