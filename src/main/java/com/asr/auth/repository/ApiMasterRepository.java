package com.asr.auth.repository;

import com.asr.auth.domain.entity.ApiMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiMasterRepository extends JpaRepository<ApiMaster, UUID> {
    Optional<ApiMaster> findByMethodAndPath(String method, String path);
}
