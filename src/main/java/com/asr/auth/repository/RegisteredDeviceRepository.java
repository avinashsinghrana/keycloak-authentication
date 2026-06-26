package com.asr.auth.repository;

import com.asr.auth.domain.entity.RegisteredDevice;
import com.asr.auth.domain.enums.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegisteredDeviceRepository extends JpaRepository<RegisteredDevice, UUID> {
    Optional<RegisteredDevice> findByUserIdAndDeviceId(UUID userId, String deviceId);
    List<RegisteredDevice> findAllByUserId(UUID userId);
    List<RegisteredDevice> findAllByUserIdAndStatus(UUID userId, DeviceStatus status);
}
