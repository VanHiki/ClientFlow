package com.clientflow.backend.domain.dashboard;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.domain.dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses/{businessId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<DashboardResponse> getDashboard(@PathVariable Long businessId) {
        return ApiResponse.<DashboardResponse>builder()
                .message("Get dashboard successfully")
                .result(dashboardService.getDashboard(businessId))
                .build();
    }
}