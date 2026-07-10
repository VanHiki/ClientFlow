package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentStatusUpdateRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/staff/appointments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffAppointmentController {

    StaffAppointmentService staffAppointmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<PageResponse<AppointmentResponse>> getAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<AppointmentResponse>>builder()
                .message("Get assigned appointments successfully")
                .result(staffAppointmentService.getAppointments(status, fromDate, toDate, pageable))
                .build();
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<AppointmentResponse> getAppointment(@PathVariable Long appointmentId) {
        return ApiResponse.<AppointmentResponse>builder()
                .message("Get assigned appointment successfully")
                .result(staffAppointmentService.getAppointment(appointmentId))
                .build();
    }

    @PatchMapping("/{appointmentId}/status")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentStatusUpdateRequest request
    ) {
        return ApiResponse.<AppointmentResponse>builder()
                .message("Appointment status updated successfully")
                .result(staffAppointmentService.updateAppointmentStatus(appointmentId, request))
                .build();
    }
}
