package com.clientflow.backend.domain.stafftimeoff;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.stafftimeoff.dto.StaffTimeOffCreateRequest;
import com.clientflow.backend.domain.stafftimeoff.dto.StaffTimeOffResponse;
import com.clientflow.backend.domain.stafftimeoff.dto.StaffTimeOffUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses/{businessId}/staff/{staffId}/time-off")
@RequiredArgsConstructor
public class StaffTimeOffController {

    private final StaffTimeOffService staffTimeOffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<StaffTimeOffResponse> createTimeOff(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @Valid @RequestBody StaffTimeOffCreateRequest request
    ) {
        return ApiResponse.<StaffTimeOffResponse>builder()
                .message("Staff time off created successfully")
                .result(staffTimeOffService.createTimeOff(businessId, staffId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<StaffTimeOffResponse>> getTimeOffs(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<StaffTimeOffResponse>>builder()
                .message("Get staff time off successfully")
                .result(staffTimeOffService.getTimeOffs(businessId, staffId, pageable))
                .build();
    }

    @PutMapping("/{timeOffId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<StaffTimeOffResponse> updateTimeOff(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @PathVariable Long timeOffId,
            @Valid @RequestBody StaffTimeOffUpdateRequest request
    ) {
        return ApiResponse.<StaffTimeOffResponse>builder()
                .message("Staff time off updated successfully")
                .result(staffTimeOffService.updateTimeOff(businessId, staffId, timeOffId, request))
                .build();
    }

    @DeleteMapping("/{timeOffId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<Void> deleteTimeOff(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @PathVariable Long timeOffId
    ) {
        staffTimeOffService.deleteTimeOff(businessId, staffId, timeOffId);

        return ApiResponse.<Void>builder()
                .message("Staff time off deleted successfully")
                .build();
    }
}
