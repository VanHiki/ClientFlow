package com.clientflow.backend.common;

import java.time.ZoneId;

public final class BookingConstants {

    public static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of(DEFAULT_TIMEZONE);
    public static final int SLOT_STEP_MINUTES = 30;

    private BookingConstants() {
    }
}
