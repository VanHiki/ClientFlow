package com.clientflow.backend.common;

import com.clientflow.backend.common.enums.AppointmentStatus;

import java.time.ZoneId;
import java.util.List;

public final class BookingConstants {

    public static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of(DEFAULT_TIMEZONE); //
    public static final int SLOT_STEP_MINUTES = 30;
    public static final List<AppointmentStatus> BLOCKING_APPOINTMENT_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.COMPLETED
    );

    private BookingConstants() {
    }
}
