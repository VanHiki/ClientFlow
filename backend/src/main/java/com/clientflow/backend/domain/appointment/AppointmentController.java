package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.domain.appointment.dto.AppointmentCreateRequest;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentStatusUpdateRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/businesses/{businessId}/appointments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentController {

    AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<AppointmentResponse> createAppointment(
            @PathVariable Long businessId,
            @Valid @RequestBody AppointmentCreateRequest request
    ) {
        return ApiResponse.<AppointmentResponse>builder()
                .message("Appointment created successfully")
                .result(appointmentService.createAppointment(businessId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<AppointmentResponse>> getAppointments(
            @PathVariable Long businessId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<AppointmentResponse>>builder()
                .message("Get appointments successfully")
                .result(appointmentService.getAppointments(
                        businessId,
                        status,
                        staffId,
                        customerId,
                        fromDate,
                        toDate,
                        pageable
                ))
                .build();
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<AppointmentResponse> getAppointment(
            @PathVariable Long businessId,
            @PathVariable Long appointmentId
    ) {
        return ApiResponse.<AppointmentResponse>builder()
                .message("Get appointment successfully")
                .result(appointmentService.getAppointment(businessId, appointmentId))
                .build();
    }

    @PatchMapping("/{appointmentId}/status")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long businessId,
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentStatusUpdateRequest request
    ) {
        return ApiResponse.<AppointmentResponse>builder()
                .message("Appointment status updated successfully")
                .result(appointmentService.updateAppointmentStatus(businessId, appointmentId, request))
                .build();
    }
}
