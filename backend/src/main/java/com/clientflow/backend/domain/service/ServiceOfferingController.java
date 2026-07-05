package com.clientflow.backend.domain.service;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.domain.service.dto.ServiceCreateRequest;
import com.clientflow.backend.domain.service.dto.ServiceResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceOfferingController {
    ServiceOfferingService serviceOfferingService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<ServiceResponse> createService(@Valid @RequestBody ServiceCreateRequest request) {
        return ApiResponse.<ServiceResponse>builder()
                .message("Service created successfully")
                .result(serviceOfferingService.createService(request))
                .build();
    }

}
