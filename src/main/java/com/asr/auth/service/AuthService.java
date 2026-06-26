package com.asr.auth.service;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.domain.entity.RegisteredDevice;
import com.asr.auth.domain.enums.DeviceStatus;
import com.asr.auth.dto.request.DeviceVerificationRequest;
import com.asr.auth.dto.request.LoginRequest;
import com.asr.auth.dto.request.RegisterRequest;
import com.asr.auth.dto.response.AuthResponse;
import com.asr.auth.event.DeviceUpdatedEvent;
import com.asr.auth.event.UserRegisteredEvent;
import com.asr.auth.exception.BusinessException;
import com.asr.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final DeviceService deviceService;
    private final KeycloakAdminService keycloakAdminService;
    private final SessionService sessionService;
    private final AuditService auditService;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void register(RegisterRequest request) {
        userService.checkDuplicates(request.getEmail(), request.getMobile());

        // 1. Create User in Keycloak
        String keycloakUserId = keycloakAdminService.createUser(
                request.getEmail(), request.getFirstName(), request.getLastName(), request.getPassword());

        // 2. Assign default role
        keycloakAdminService.assignRole(keycloakUserId, "CUSTOMER");

        // 3. Save AppUser
        AppUser user = userMapper.toEntity(request);
        user.setKeycloakId(keycloakUserId);
        user = userService.saveUser(user);

        // 4. Register Device (Pending Verification)
        deviceService.registerDevice(user, request);

        // 5. Audit Log
        auditService.logAction(user, "REGISTER", "User registered successfully", null);

        // 6. Publish Event
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send("auth-events", event);
        log.info("User registered and event published for {}", user.getEmail());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AppUser user = userService.findByEmailOrThrow(request.getUsername());

        // 1. Validate Device
        RegisteredDevice device = deviceService.findDevice(user.getId(), request.getDeviceId())
                .orElseThrow(() -> new BusinessException("DEVICE_VERIFICATION_REQUIRED"));

        if (device.getStatus() == DeviceStatus.PENDING_VERIFICATION) {
            throw new BusinessException("DEVICE_VERIFICATION_REQUIRED");
        }
        if (device.getStatus() == DeviceStatus.BLOCKED || device.getStatus() == DeviceStatus.LOST) {
            throw new BusinessException("Device is blocked or lost");
        }

        // 2. Authenticate with Keycloak
        AuthResponse authResponse = keycloakAdminService.authenticateUser(request.getUsername(), request.getPassword());

        // 3. Create Session
        sessionService.createSession(user, device, authResponse);

        // 4. Audit Log
        auditService.logAction(user, "LOGIN", "User logged in from device " + request.getDeviceId(), null);

        return authResponse;
    }

    @Transactional
    public void verifyDevice(DeviceVerificationRequest request) {
        AppUser user = userService.findByEmailOrThrow(request.getIdentifier());
        RegisteredDevice device = deviceService.findDevice(user.getId(), request.getDeviceId())
                .orElseThrow(() -> new BusinessException("Device not found"));

        // Simulate OTP verification (In real scenario, check OTP against cache/db)
        if (!"123456".equals(request.getOtp())) { // Mock OTP for simplicity
            throw new BusinessException("Invalid OTP");
        }

        deviceService.verifyDevice(device);
        
        auditService.logAction(user, "DEVICE_VERIFIED", "Device " + request.getDeviceId() + " verified", null);

        DeviceUpdatedEvent event = DeviceUpdatedEvent.builder()
                .userId(user.getId().toString())
                .deviceId(device.getDeviceId())
                .action("VERIFIED")
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send("auth-events", event);
    }
}
