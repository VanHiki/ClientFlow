package com.clientflow.backend.domain.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        long totalCustomers,
        long totalServices,
        long totalStaff,
        LocalDate fromDate,
        LocalDate toDate,
        long totalAppointments,
        long todayAppointments,
        long pendingAppointments,
        long confirmedAppointments,
        long completedAppointments,
        long cancelledAppointments,
        BigDecimal completedRevenue,
        List<TopServiceResponse> topServices,
        List<UpcomingAppointmentResponse> upcomingAppointments
) {
}
