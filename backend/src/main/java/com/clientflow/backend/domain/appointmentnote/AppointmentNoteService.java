package com.clientflow.backend.domain.appointmentnote;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.appointment.Appointment;
import com.clientflow.backend.domain.appointment.AppointmentRepository;
import com.clientflow.backend.domain.appointmentnote.dto.AppointmentNoteCreateRequest;
import com.clientflow.backend.domain.appointmentnote.dto.AppointmentNoteResponse;
import com.clientflow.backend.domain.appointmentnote.dto.AppointmentNoteUpdateRequest;
import com.clientflow.backend.domain.appointmentnote.mapper.AppointmentNoteMapper;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentNoteService {

    AppointmentNoteRepository appointmentNoteRepository;
    AppointmentRepository appointmentRepository;
    BusinessRepository businessRepository;
    AppointmentNoteMapper appointmentNoteMapper;
    SecurityUtil securityUtil;

    @Transactional
    public AppointmentNoteResponse createNote(
            Long businessId,
            Long appointmentId,
            AppointmentNoteCreateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        Appointment appointment = getAppointment(business.getId(), appointmentId);
        User author = securityUtil.getCurrentUser();

        AppointmentNote note = AppointmentNote.builder()
                .appointment(appointment)
                .author(author)
                .content(request.content().trim())
                .build();

        return appointmentNoteMapper.toResponse(appointmentNoteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentNoteResponse> getNotes(Long businessId, Long appointmentId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);
        Appointment appointment = getAppointment(business.getId(), appointmentId);

        return PageResponse.from(
                appointmentNoteRepository.findByAppointmentId(appointment.getId(), pageable)
                        .map(appointmentNoteMapper::toResponse)
        );
    }

    @Transactional
    public AppointmentNoteResponse updateNote(
            Long businessId,
            Long appointmentId,
            Long noteId,
            AppointmentNoteUpdateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        Appointment appointment = getAppointment(business.getId(), appointmentId);
        AppointmentNote note = getNote(appointment.getId(), noteId);

        note.setContent(request.content().trim());

        return appointmentNoteMapper.toResponse(note);
    }

    @Transactional
    public void deleteNote(Long businessId, Long appointmentId, Long noteId) {
        Business business = getCurrentOwnerBusiness(businessId);
        Appointment appointment = getAppointment(business.getId(), appointmentId);
        AppointmentNote note = getNote(appointment.getId(), noteId);

        appointmentNoteRepository.delete(note);
    }

    private Appointment getAppointment(Long businessId, Long appointmentId) {
        return appointmentRepository.findByIdAndBusinessId(appointmentId, businessId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
    }

    private AppointmentNote getNote(Long appointmentId, Long noteId) {
        return appointmentNoteRepository.findByIdAndAppointmentId(noteId, appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOTE_NOT_FOUND));
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }
}
