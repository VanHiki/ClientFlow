package com.clientflow.backend.domain.dashboard.dto;

import java.util.List;

public record DashboardResponse(
        long totalCustomers,
        long totalServices,
        long totalStaff,
        long totalAppointments,
        long todayAppointments,
        long pendingAppointments,
        long confirmedAppointments,
        long completedAppointments,
        List<UpcomingAppointmentResponse> upcomingAppointments
) {
}
