package com.clientflow.backend.domain.workinghour;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.workinghour.dto.WorkingHourCreateRequest;
import com.clientflow.backend.domain.workinghour.dto.WorkingHourResponse;
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
@RequestMapping("/api/businesses/{businessId}/staff/{staffId}/working-hours")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkingHourController {

    WorkingHourService workingHourService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<WorkingHourResponse> createWorkingHour(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @Valid @RequestBody WorkingHourCreateRequest request
    ) {
        return ApiResponse.<WorkingHourResponse>builder()
                .message("Working hour created successfully")
                .result(workingHourService.createWorkingHour(businessId, staffId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<WorkingHourResponse>> getWorkingHours(
            @PathVariable Long businessId,
            @PathVariable Long staffId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<WorkingHourResponse>>builder()
                .message("Get working hours successfully")
                .result(workingHourService.getWorkingHours(businessId, staffId, pageable))
                .build();
    }
}