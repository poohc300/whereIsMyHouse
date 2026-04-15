package com.example.housing.user.controller;

import com.example.housing.auth.security.CustomUserDetails;
import com.example.housing.user.dto.UserProfileRequest;
import com.example.housing.user.dto.UserProfileResponse;
import com.example.housing.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userProfileService.getProfile(userDetails.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> saveProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.saveProfile(userDetails.getId(), request));
    }
}
