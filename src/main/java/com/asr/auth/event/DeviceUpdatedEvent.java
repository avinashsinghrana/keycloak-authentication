package com.asr.auth.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUpdatedEvent {
    private String userId;
    private String deviceId;
    private String action;
    private LocalDateTime timestamp;
}
