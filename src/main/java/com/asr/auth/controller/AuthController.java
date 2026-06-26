package com.asr.auth.controller;

import com.asr.auth.dto.request.DeviceVerificationRequest;
import com.asr.auth.dto.request.LoginRequest;
import com.asr.auth.dto.request.RegisterRequest;
import com.asr.auth.dto.response.ApiResponse;
import com.asr.auth.dto.response.AuthResponse;
import com.asr.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(null, "User registered successfully. Pending device verification."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/device/verify")
    public ResponseEntity<ApiResponse<Void>> verifyDevice(@Valid @RequestBody DeviceVerificationRequest request) {
        authService.verifyDevice(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Device verified successfully"));
    }
}
