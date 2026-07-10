package com.clientflow.backend.domain.service;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.service.dto.ServiceCreateRequest;
import com.clientflow.backend.domain.service.dto.ServiceResponse;
import com.clientflow.backend.domain.service.dto.ServiceStatusUpdateRequest;
import com.clientflow.backend.domain.service.dto.ServiceUpdateRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses/{businessId}/services")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceOfferingController {
    ServiceOfferingService serviceOfferingService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<ServiceResponse> createService(
            @PathVariable Long businessId,
            @Valid @RequestBody ServiceCreateRequest request
    ) {
        return ApiResponse.<ServiceResponse>builder()
                .message("Service created successfully")
                .result(serviceOfferingService.createService(businessId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<ServiceResponse>> getServices(
            @PathVariable Long businessId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<ServiceResponse>>builder()
                .message("Get services successfully")
                .result(serviceOfferingService.getServices(businessId, keyword, active, pageable))
                .build();
    }

    @PutMapping("/{serviceId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<ServiceResponse> updateService(
            @PathVariable Long businessId,
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceUpdateRequest request
    ) {
        return ApiResponse.<ServiceResponse>builder()
                .message("Service updated successfully")
                .result(serviceOfferingService.updateService(businessId, serviceId, request))
                .build();
    }

    @PatchMapping("/{serviceId}/status")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<ServiceResponse> updateServiceStatus(
            @PathVariable Long businessId,
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceStatusUpdateRequest request
    ) {
        return ApiResponse.<ServiceResponse>builder()
                .message("Service status updated successfully")
                .result(serviceOfferingService.updateServiceStatus(businessId, serviceId, request))
                .build();
    }

}
