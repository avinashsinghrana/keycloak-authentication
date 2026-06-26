package com.asr.auth.service;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.domain.entity.RegisteredDevice;
import com.asr.auth.domain.enums.DeviceStatus;
import com.asr.auth.dto.request.RegisterRequest;
import com.asr.auth.exception.BusinessException;
import com.asr.auth.mapper.DeviceMapper;
import com.asr.auth.repository.RegisteredDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final RegisteredDeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    @Transactional
    public RegisteredDevice registerDevice(AppUser user, RegisterRequest request) {
        // Enforce One Active Device Policy if required, or simply register a new device
        // Here we just register the initial device as PENDING_VERIFICATION
        
        Optional<RegisteredDevice> existingDevice = deviceRepository.findByUserIdAndDeviceId(user.getId(), request.getDeviceId());
        if (existingDevice.isPresent()) {
            throw new BusinessException("Device already registered for this user");
        }

        RegisteredDevice device = deviceMapper.toEntity(request);
        device.setUser(user);
        
        // As per requirements: One Active Device Policy. We can block other devices when this one becomes ACTIVE,
        // but for now during registration it is PENDING_VERIFICATION.
        
        return deviceRepository.save(device);
    }

    public Optional<RegisteredDevice> findDevice(UUID userId, String deviceId) {
        return deviceRepository.findByUserIdAndDeviceId(userId, deviceId);
    }

    @Transactional
    public void verifyDevice(RegisteredDevice device) {
        device.setStatus(DeviceStatus.ACTIVE);
        device.setIsTrusted(true);
        deviceRepository.save(device);
        
        // Implement One Active Device Policy: Deactivate other active devices
        List<RegisteredDevice> otherDevices = deviceRepository.findAllByUserIdAndStatus(device.getUser().getId(), DeviceStatus.ACTIVE);
        for (RegisteredDevice other : otherDevices) {
            if (!other.getId().equals(device.getId())) {
                other.setStatus(DeviceStatus.BLOCKED);
                deviceRepository.save(other);
                log.info("Blocked previous active device {} for user {}", other.getDeviceId(), device.getUser().getId());
            }
        }
    }
}
