package com.asr.auth.mapper;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.dto.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING_VERIFICATION")
    @Mapping(target = "keycloakId", ignore = true)
    AppUser toEntity(RegisterRequest request);
}
