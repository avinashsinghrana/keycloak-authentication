package com.asr.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class ApiMappingRequest {
    @NotBlank(message = "Role name is required")
    private String roleName;

    private UUID apiId;
}
