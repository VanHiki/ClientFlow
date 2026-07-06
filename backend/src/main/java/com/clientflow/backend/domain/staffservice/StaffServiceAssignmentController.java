package com.clientflow.backend.domain.staffservice;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.staffservice.dto.StaffServiceResponse;
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
@RequestMapping("/api/businesses/{businessId}/staff/{staffId}/services")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffServiceAssignmentController {

    StaffServiceAssignmentService staffServiceAssignmentService;

    @PostMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<StaffServiceResponse> assignService(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @PathVariable Long serviceId
    ) {
        return ApiResponse.<StaffServiceResponse>builder()
                .message("Service assigned to staff successfully")
                .result(staffServiceAssignmentService.assignService(businessId, staffId, serviceId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<StaffServiceResponse>> getStaffServices(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<StaffServiceResponse>>builder()
                .message("Get staff services successfully")
                .result(staffServiceAssignmentService.getStaffServices(businessId, staffId, pageable))
                .build();
    }
}