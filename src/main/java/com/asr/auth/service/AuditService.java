package com.asr.auth.service;

import com.asr.auth.domain.entity.AuditLog;
import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void logAction(AppUser user, String action, String details, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(log);
    }

    @Async
    public void logAction(String action, String details, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(log);
    }
}
