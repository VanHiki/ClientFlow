package com.clientflow.backend.domain.appointment;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingCodeService {

    static final char[] ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
    static final int RANDOM_LENGTH = 10;
    static final int MAX_GENERATION_ATTEMPTS = 10;
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    AppointmentRepository appointmentRepository;
    SecureRandom secureRandom = new SecureRandom();

    public String generate(LocalDate appointmentDate) {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String code = "CF-" + appointmentDate.format(DATE_FORMAT) + "-" + randomPart();

            if (!appointmentRepository.existsByBookingCode(code)) {
                return code;
            }
        }

        throw new IllegalStateException("Cannot generate a unique booking code");
    }

    private String randomPart() {
        StringBuilder value = new StringBuilder(RANDOM_LENGTH);

        for (int index = 0; index < RANDOM_LENGTH; index++) {
            value.append(ALPHABET[secureRandom.nextInt(ALPHABET.length)]);
        }

        return value.toString();
    }
}
