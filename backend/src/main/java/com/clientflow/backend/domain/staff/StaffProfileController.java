package com.clientflow.backend.domain.staff;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.staff.dto.StaffCreateRequest;
import com.clientflow.backend.domain.staff.dto.StaffAccountCreateRequest;
import com.clientflow.backend.domain.staff.dto.StaffResponse;
import com.clientflow.backend.domain.staff.dto.StaffStatusUpdateRequest;
import com.clientflow.backend.domain.staff.dto.StaffUpdateRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/businesses/{businessId}/staff")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffProfileController {

    StaffProfileService staffProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<StaffResponse> createStaff(
            @PathVariable Long businessId,
            @Valid @RequestBody StaffCreateRequest request
    ) {
        return ApiResponse.<StaffResponse>builder()
                .message("Staff created successfully")
                .result(staffProfileService.createStaff(businessId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<StaffResponse>> getStaff(
            @PathVariable Long businessId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<StaffResponse>>builder()
                .message("Get staff successfully")
                .result(staffProfileService.getStaff(businessId, keyword, active, pageable))
                .build();
    }

    @PutMapping("/{staffId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<StaffResponse> updateStaff(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @Valid @RequestBody StaffUpdateRequest request
    ) {
        return ApiResponse.<StaffResponse>builder()
                .message("Staff updated successfully")
                .result(staffProfileService.updateStaff(businessId, staffId, request))
                .build();
    }

    @PatchMapping("/{staffId}/status")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<StaffResponse> updateStaffStatus(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @Valid @RequestBody StaffStatusUpdateRequest request
    ) {
        return ApiResponse.<StaffResponse>builder()
                .message("Staff status updated successfully")
                .result(staffProfileService.updateStaffStatus(businessId, staffId, request))
                .build();
    }

    @PostMapping("/{staffId}/account")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<StaffResponse> createStaffAccount(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @Valid @RequestBody StaffAccountCreateRequest request
    ) {
        return ApiResponse.<StaffResponse>builder()
                .message("Staff login account created successfully")
                .result(staffProfileService.createStaffAccount(businessId, staffId, request))
                .build();
    }
}
