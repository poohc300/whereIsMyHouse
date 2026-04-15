package com.example.housing.auth.controller;

import com.example.housing.auth.dto.LoginRequest;
import com.example.housing.auth.dto.LoginResponse;
import com.example.housing.auth.security.CustomUserDetails;
import com.example.housing.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String token = jwtTokenProvider.generateAccessToken(
                userDetails.getId(),
                userDetails.getUsername()
            );

            return ResponseEntity.ok(new LoginResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                token
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
