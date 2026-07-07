package com.clientflow.backend.domain.publicbooking;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.availability.AvailabilityService;
import com.clientflow.backend.domain.availability.dto.AvailableSlotResponse;
import com.clientflow.backend.domain.publicbooking.dto.PublicAppointmentCreateRequest;
import com.clientflow.backend.domain.publicbooking.dto.PublicBusinessResponse;
import com.clientflow.backend.domain.publicbooking.dto.PublicServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public/businesses/{slug}")
@RequiredArgsConstructor
public class PublicBookingController {

    private final PublicBookingService publicBookingService;
    private final AvailabilityService availabilityService;

    @GetMapping
    public ApiResponse<PublicBusinessResponse> getBusiness(@PathVariable String slug) {
        return ApiResponse.<PublicBusinessResponse>builder()
                .message("Get public business successfully")
                .result(publicBookingService.getBusiness(slug))
                .build();
    }

    @GetMapping("/services")
    public ApiResponse<List<PublicServiceResponse>> getServices(@PathVariable String slug) {
        return ApiResponse.<List<PublicServiceResponse>>builder()
                .message("Get public services successfully")
                .result(publicBookingService.getServices(slug))
                .build();
    }
    @GetMapping("/available-slots")
    public ApiResponse<List<AvailableSlotResponse>> getAvailableSlots(
            @PathVariable String slug,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.<List<AvailableSlotResponse>>builder()
                .message("Get public available slots successfully")
                .result(availabilityService.getPublicAvailableSlots(slug, serviceId, date))
                .build();
    }

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AppointmentResponse> createAppointment(
            @PathVariable String slug,
            @Valid @RequestBody PublicAppointmentCreateRequest request
    ) {
        return ApiResponse.<AppointmentResponse>builder()
                .message("Appointment booked successfully")
                .result(publicBookingService.createAppointment(slug, request))
                .build();
    }
}