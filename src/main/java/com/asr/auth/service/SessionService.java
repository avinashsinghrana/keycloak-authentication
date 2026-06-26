package com.asr.auth.service;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.domain.entity.RegisteredDevice;
import com.asr.auth.domain.entity.UserSession;
import com.asr.auth.dto.response.AuthResponse;
import com.asr.auth.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository sessionRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Transactional
    public void createSession(AppUser user, RegisteredDevice device, AuthResponse authResponse) {
        // Hash refresh token for security if storing it, here we just store a prefix or hash 
        // to identify the session or we store null since Keycloak manages the actual tokens.
        // We track the session in our DB.
        
        UserSession session = UserSession.builder()
                .user(user)
                .device(device)
                .expiresAt(LocalDateTime.now().plusSeconds(authResponse.getRefreshExpiresIn()))
                .build();
                
        sessionRepository.save(session);
    }

    @Transactional
    public void logoutAllDevices(AppUser user) {
        sessionRepository.deleteByUserId(user.getId());
        keycloakAdminService.logoutUser(user.getKeycloakId());
    }

    @Transactional
    public void logoutDevice(RegisteredDevice device) {
        sessionRepository.deleteByDeviceId(device.getId());
        // Note: Keycloak logout usually logs out all sessions for the user globally,
        // so device specific logout with Keycloak might require revoking specific refresh tokens.
        // For simplicity, we just clear it from our local DB, requiring re-auth next time.
    }
}
