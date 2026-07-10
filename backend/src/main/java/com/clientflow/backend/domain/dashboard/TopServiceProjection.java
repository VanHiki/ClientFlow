package com.clientflow.backend.domain.dashboard;

public interface TopServiceProjection {

    Long getServiceId();

    String getServiceName();

    long getBookingCount();
}
