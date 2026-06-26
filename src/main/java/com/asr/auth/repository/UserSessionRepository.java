package com.asr.auth.repository;

import com.asr.auth.domain.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    List<UserSession> findAllByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    void deleteByDeviceId(UUID deviceId);
}
