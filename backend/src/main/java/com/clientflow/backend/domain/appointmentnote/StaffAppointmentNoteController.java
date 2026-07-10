package com.clientflow.backend.domain.appointmentnote;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.appointmentnote.dto.AppointmentNoteCreateRequest;
import com.clientflow.backend.domain.appointmentnote.dto.AppointmentNoteResponse;
import com.clientflow.backend.domain.appointmentnote.dto.AppointmentNoteUpdateRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff/appointments/{appointmentId}/notes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffAppointmentNoteController {

    AppointmentNoteService appointmentNoteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<AppointmentNoteResponse> createNote(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentNoteCreateRequest request
    ) {
        return ApiResponse.<AppointmentNoteResponse>builder()
                .message("Appointment note created successfully")
                .result(appointmentNoteService.createStaffNote(appointmentId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<PageResponse<AppointmentNoteResponse>> getNotes(
            @PathVariable Long appointmentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<AppointmentNoteResponse>>builder()
                .message("Get appointment notes successfully")
                .result(appointmentNoteService.getStaffNotes(appointmentId, pageable))
                .build();
    }

    @PutMapping("/{noteId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<AppointmentNoteResponse> updateNote(
            @PathVariable Long appointmentId,
            @PathVariable Long noteId,
            @Valid @RequestBody AppointmentNoteUpdateRequest request
    ) {
        return ApiResponse.<AppointmentNoteResponse>builder()
                .message("Appointment note updated successfully")
                .result(appointmentNoteService.updateStaffNote(appointmentId, noteId, request))
                .build();
    }

    @DeleteMapping("/{noteId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ApiResponse<Void> deleteNote(
            @PathVariable Long appointmentId,
            @PathVariable Long noteId
    ) {
        appointmentNoteService.deleteStaffNote(appointmentId, noteId);

        return ApiResponse.<Void>builder()
                .message("Appointment note deleted successfully")
                .build();
    }
}
