package com.asr.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApiMasterRequest {
    @NotBlank(message = "Method is required")
    private String method;

    @NotBlank(message = "Path is required")
    private String path;

    private String description;
}
