package com.clientflow.backend.domain.availability;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.domain.availability.dto.AvailableSlotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/businesses/{businessId}/available-slots")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<List<AvailableSlotResponse>> getAvailableSlots(
            @PathVariable Long businessId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.<List<AvailableSlotResponse>>builder()
                .message("Get available slots successfully")
                .result(availabilityService.getAvailableSlots(businessId, serviceId, date))
                .build();
    }
}