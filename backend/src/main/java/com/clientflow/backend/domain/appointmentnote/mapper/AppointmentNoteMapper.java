package com.clientflow.backend.domain.appointmentnote.mapper;

import com.clientflow.backend.domain.appointmentnote.AppointmentNote;
import com.clientflow.backend.domain.appointmentnote.dto.AppointmentNoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentNoteMapper {

    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "authorUserId", source = "author.id")
    @Mapping(target = "authorName", source = "author.fullName")
    AppointmentNoteResponse toResponse(AppointmentNote appointmentNote);
}
