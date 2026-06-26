package com.asr.auth.controller;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.dto.response.ApiResponse;
import com.asr.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AppUser>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("preferred_username");
        AppUser user = userService.findByEmailOrThrow(email);
        return ResponseEntity.ok(ApiResponse.success(user, "User profile fetched successfully"));
    }
}
