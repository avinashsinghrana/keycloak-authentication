package com.asr.auth.service;

import com.asr.auth.domain.entity.ApiMaster;
import com.asr.auth.domain.entity.RoleApiMapping;
import com.asr.auth.dto.request.ApiMappingRequest;
import com.asr.auth.dto.request.ApiMasterRequest;
import com.asr.auth.exception.BusinessException;
import com.asr.auth.repository.ApiMasterRepository;
import com.asr.auth.repository.RoleApiMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleApiService {

    private final RoleApiMappingRepository roleApiMappingRepository;
    private final ApiMasterRepository apiMasterRepository;

    @Cacheable(value = "roleApiMappings", key = "#roleName")
    public List<RoleApiMapping> getRoleApiMappings(String roleName) {
        return roleApiMappingRepository.findByRoleName(roleName);
    }

    @Transactional
    public ApiMaster registerApi(ApiMasterRequest request) {
        if (apiMasterRepository.findByMethodAndPath(request.getMethod().toUpperCase(), request.getPath()).isPresent()) {
            throw new BusinessException("API already registered with this method and path");
        }

        ApiMaster apiMaster = ApiMaster.builder()
                .method(request.getMethod().toUpperCase())
                .path(request.getPath())
                .description(request.getDescription())
                .build();

        return apiMasterRepository.save(apiMaster);
    }

    @Transactional
    @CacheEvict(value = "roleApiMappings", key = "#request.roleName")
    public RoleApiMapping assignApiToRole(ApiMappingRequest request) {
        ApiMaster apiMaster = apiMasterRepository.findById(request.getApiId())
                .orElseThrow(() -> new BusinessException("API not found"));

        if (roleApiMappingRepository.findByRoleNameAndApiMasterId(request.getRoleName(), request.getApiId()).isPresent()) {
            throw new BusinessException("Role is already mapped to this API");
        }

        RoleApiMapping mapping = RoleApiMapping.builder()
                .roleName(request.getRoleName())
                .apiMaster(apiMaster)
                .build();

        return roleApiMappingRepository.save(mapping);
    }

    @Transactional
    @CacheEvict(value = "roleApiMappings", key = "#roleName")
    public void removeApiFromRole(String roleName, UUID apiId) {
        RoleApiMapping mapping = roleApiMappingRepository.findByRoleNameAndApiMasterId(roleName, apiId)
                .orElseThrow(() -> new BusinessException("Mapping not found"));
        
        roleApiMappingRepository.delete(mapping);
    }

    public List<ApiMaster> getAllApis() {
        return apiMasterRepository.findAll();
    }
}
