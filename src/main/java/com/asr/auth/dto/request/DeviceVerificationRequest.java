package com.asr.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceVerificationRequest {
    @NotBlank(message = "User ID or Email is required")
    private String identifier;

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    @NotBlank(message = "OTP is required")
    private String otp;
}
