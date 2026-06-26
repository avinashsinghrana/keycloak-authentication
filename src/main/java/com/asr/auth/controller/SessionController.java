package com.asr.auth.controller;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.dto.response.ApiResponse;
import com.asr.auth.service.SessionService;
import com.asr.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;

    @PostMapping("/logout/all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("preferred_username");
        AppUser user = userService.findByEmailOrThrow(email);
        sessionService.logoutAllDevices(user);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out from all devices"));
    }
}
