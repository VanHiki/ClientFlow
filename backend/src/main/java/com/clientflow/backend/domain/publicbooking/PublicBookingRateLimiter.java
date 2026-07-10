package com.clientflow.backend.domain.publicbooking;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PublicBookingRateLimiter {

    private static final long CLEANUP_INTERVAL = 1_000;

    private final int maxBookingsPerHour;
    private final Clock clock;
    private final ConcurrentHashMap<String, BookingWindow> windows = new ConcurrentHashMap<>();
    private final AtomicLong requestCount = new AtomicLong();

    public PublicBookingRateLimiter(
            @Value("${clientflow.booking.max-bookings-per-hour:3}") int maxBookingsPerHour
    ) {
        this(maxBookingsPerHour, Clock.systemUTC());
    }

    PublicBookingRateLimiter(int maxBookingsPerHour, Clock clock) {
        this.maxBookingsPerHour = maxBookingsPerHour;
        this.clock = clock;
    }

    public void checkAllowed(String businessSlug, String customerPhone) {
        Instant now = clock.instant();
        String key = businessSlug.trim().toLowerCase(Locale.ROOT) + ":" + customerPhone.trim();
        AtomicBoolean rejected = new AtomicBoolean(false);

        windows.compute(key, (ignored, current) -> {
            if (current == null || !now.isBefore(current.expiresAt())) {
                return new BookingWindow(1, now.plus(1, ChronoUnit.HOURS));
            }

            if (current.count() >= maxBookingsPerHour) {
                rejected.set(true);
                return current;
            }

            return new BookingWindow(current.count() + 1, current.expiresAt());
        });

        cleanupExpiredWindows(now);

        if (rejected.get()) {
            throw new AppException(ErrorCode.PUBLIC_BOOKING_RATE_LIMITED);
        }
    }

    private void cleanupExpiredWindows(Instant now) {
        if (requestCount.incrementAndGet() % CLEANUP_INTERVAL == 0) {
            windows.entrySet().removeIf(entry -> !now.isBefore(entry.getValue().expiresAt()));
        }
    }

    private record BookingWindow(int count, Instant expiresAt) {
    }
}
