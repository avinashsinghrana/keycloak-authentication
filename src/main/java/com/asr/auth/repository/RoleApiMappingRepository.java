package com.asr.auth.repository;

import com.asr.auth.domain.entity.RoleApiMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleApiMappingRepository extends JpaRepository<RoleApiMapping, UUID> {
    List<RoleApiMapping> findByRoleName(String roleName);
    Optional<RoleApiMapping> findByRoleNameAndApiMasterId(String roleName, UUID apiId);
}
