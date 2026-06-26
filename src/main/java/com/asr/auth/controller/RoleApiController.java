package com.asr.auth.controller;

import com.asr.auth.domain.entity.ApiMaster;
import com.asr.auth.domain.entity.RoleApiMapping;
import com.asr.auth.dto.request.ApiMappingRequest;
import com.asr.auth.dto.request.ApiMasterRequest;
import com.asr.auth.dto.response.ApiResponse;
import com.asr.auth.service.RoleApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
public class RoleApiController {

    private final RoleApiService roleApiService;

    @GetMapping("/{roleName}/apis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RoleApiMapping>>> getRoleApis(@PathVariable String roleName) {
        List<RoleApiMapping> mappings = roleApiService.getRoleApiMappings(roleName);
        return ResponseEntity.ok(ApiResponse.success(mappings, "Fetched API mappings for role"));
    }

    @PostMapping("/api-master")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApiMaster>> registerApi(@Valid @RequestBody ApiMasterRequest request) {
        ApiMaster api = roleApiService.registerApi(request);
        return ResponseEntity.ok(ApiResponse.success(api, "API registered successfully"));
    }

    @GetMapping("/api-master")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApiMaster>>> getAllApis() {
        List<ApiMaster> apis = roleApiService.getAllApis();
        return ResponseEntity.ok(ApiResponse.success(apis, "Fetched all registered APIs"));
    }

    @PostMapping("/assign-api")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleApiMapping>> assignApiToRole(@Valid @RequestBody ApiMappingRequest request) {
        RoleApiMapping mapping = roleApiService.assignApiToRole(request);
        return ResponseEntity.ok(ApiResponse.success(mapping, "API assigned to role successfully"));
    }

    @DeleteMapping("/{roleName}/apis/{apiId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeApiFromRole(@PathVariable String roleName, @PathVariable UUID apiId) {
        roleApiService.removeApiFromRole(roleName, apiId);
        return ResponseEntity.ok(ApiResponse.success(null, "API removed from role successfully"));
    }
}
