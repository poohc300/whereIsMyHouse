package com.example.housing.user.mapper;

import com.example.housing.user.model.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper {

    UserProfile findByUserId(Long userId);
    void insert(UserProfile profile);
    void update(UserProfile profile);
}
