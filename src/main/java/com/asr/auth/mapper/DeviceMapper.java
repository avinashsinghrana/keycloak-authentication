package com.asr.auth.mapper;

import com.asr.auth.domain.entity.RegisteredDevice;
import com.asr.auth.dto.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING_VERIFICATION")
    @Mapping(target = "isTrusted", constant = "false")
    @Mapping(target = "user", ignore = true)
    RegisteredDevice toEntity(RegisterRequest request);
}
