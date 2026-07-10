package com.clientflow.backend.domain.publicbooking;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicBookingRateLimiterTest {

    @Test
    void rejectsFourthAttemptInSameWindow() {
        Clock clock = Clock.fixed(Instant.parse("2026-07-10T10:00:00Z"), ZoneOffset.UTC);
        PublicBookingRateLimiter rateLimiter = new PublicBookingRateLimiter(3, clock);

        assertDoesNotThrow(() -> rateLimiter.checkAllowed("hiki-salon", "0909000001"));
        assertDoesNotThrow(() -> rateLimiter.checkAllowed("hiki-salon", "0909000001"));
        assertDoesNotThrow(() -> rateLimiter.checkAllowed("hiki-salon", "0909000001"));

        AppException exception = assertThrows(
                AppException.class,
                () -> rateLimiter.checkAllowed("hiki-salon", "0909000001")
        );

        assertEquals(ErrorCode.PUBLIC_BOOKING_RATE_LIMITED, exception.getErrorCode());
    }
}
