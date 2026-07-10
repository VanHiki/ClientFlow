# ClientFlow Booking Rules and Enums

This document freezes the first MVP rules for booking, slot generation, appointment status, and shared enums.

## Fixed MVP Decisions

| Topic | Decision |
| --- | --- |
| Default timezone | Asia/Ho_Chi_Minh |
| Slot step | 30 minutes |
| Appointment storage | appointment_date DATE, start_time TIME, end_time TIME |
| Default new appointment status | PENDING |
| Public booking login | Not required |
| Booking code | Required and unique |
| Customer creation | Create customer inside the selected business if not found |
| Conflict scope | Same staff, same date, overlapping time range |

## Appointment Status

```text
PENDING
CONFIRMED
CHECKED_IN
COMPLETED
CANCELLED
NO_SHOW
```

### Status Meaning

| Status | Meaning | Blocks Slot? |
| --- | --- | --- |
| PENDING | Booking has been created and is waiting for owner/staff confirmation | Yes |
| CONFIRMED | Booking is accepted | Yes |
| CHECKED_IN | Customer has arrived | Yes |
| COMPLETED | Service has been completed | No for future slot generation |
| CANCELLED | Booking has been cancelled | No |
| NO_SHOW | Customer did not arrive | No |

### Allowed Transitions

```text
PENDING -> CONFIRMED
PENDING -> CANCELLED

CONFIRMED -> CHECKED_IN
CONFIRMED -> CANCELLED
CONFIRMED -> NO_SHOW

CHECKED_IN -> COMPLETED
CHECKED_IN -> CANCELLED

COMPLETED -> no further status change
CANCELLED -> no further status change
NO_SHOW -> no further status change
```

## Slot Generation Rules

Given:

- businessSlug
- serviceId
- appointmentDate

The backend must:

1. Resolve business by slug.
2. Reject if business does not exist or is inactive.
3. Resolve service by id and business_id.
4. Reject if service does not exist or is inactive.
5. Find active staff who can provide the service through staff_services.
6. Find each staff member's active working_hours for appointmentDate.dayOfWeek.
7. Ignore staff if the business is closed on that date.
8. Remove staff availability covered by staff_time_off.
9. Generate candidate start times using a 30-minute step.
10. Ensure start_time + service.duration_minutes <= working_hours.end_time.
11. Remove slots in the past using Asia/Ho_Chi_Minh.
12. Remove slots overlapping appointments with blocking statuses.
13. Return available slots grouped by staff or flattened with staff info.

## Conflict Rules

Two appointments conflict when all conditions are true:

```text
same staff_id
same appointment_date
existing status in PENDING, CONFIRMED, CHECKED_IN
new_start < existing_end
new_end > existing_start
```

Examples:

```text
09:00-10:00 conflicts with 09:30-10:30
09:00-10:00 conflicts with 08:30-09:30
09:00-10:00 does not conflict with 10:00-11:00
09:00-10:00 does not conflict with 08:00-09:00
```

When creating an appointment:

- Always recompute and re-check conflict in a transaction.
- Do not trust slots previously returned by the available-slots API.
- If two requests book the same staff and slot at the same time, only one request may succeed.

## Public Booking Validation

Public booking request must reject when:

- Appointment date is in the past.
- Start time is in the past for today's date.
- Business is inactive.
- Service is inactive.
- Staff is inactive.
- Staff cannot provide the selected service.
- Slot is outside working hours.
- Slot overlaps blocking appointment.
- Slot overlaps staff time off.
- Date is a business closed day.
- Customer name is blank.
- Customer phone is invalid or blank.
- Customer email is provided but invalid.

Special customer matching rule:

- If public booking finds an existing inactive customer by phone, reactivate that customer before creating the appointment.
- Every new appointment receives a unique public booking code.
- Public cancellation is allowed for PENDING or CONFIRMED appointments at least 2 hours before start time.

## Role Enum

```text
OWNER
STAFF
CUSTOMER
ADMIN
```

MVP role rules:

- OWNER manages their own business data.
- STAFF sees and updates appointments assigned to their staff profile.
- CUSTOMER is optional for MVP because public booking does not require login.
- ADMIN is reserved for system management.

## Business Exception Type Enum

```text
CLOSED_DAY
HOLIDAY
SPECIAL_CLOSURE
```

Meaning:

- CLOSED_DAY: regular or planned business-wide closure.
- HOLIDAY: public holiday or seasonal holiday.
- SPECIAL_CLOSURE: one-off closure such as maintenance or private event.

## Notification Type Enum

```text
APPOINTMENT_CREATED
APPOINTMENT_CONFIRMED
APPOINTMENT_CANCELLED
APPOINTMENT_COMPLETED
APPOINTMENT_REMINDER
PASSWORD_RESET_REQUESTED
```

MVP only stores notifications in the database. Real email/SMS delivery is not required.

## Timezone Rules

- All booking calculations use Asia/Ho_Chi_Minh.
- The database stores appointment_date, start_time, and end_time as business-local values.
- Do not compare booking time with server default timezone.
- Use Clock or ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")) in services so tests can control current time later.

## Deletion Rules

- Do not hard-delete services that have appointments.
- Prefer active=false for services and staff.
- Prefer active=false for customers instead of hard-delete.
- Owner-created appointments cannot use inactive customers.
- Public booking may reactivate an inactive customer matched by phone.
- Cancelled appointments remain in customer history.
- NO_SHOW appointments remain in customer history.
- COMPLETED appointments count toward revenue.
- CANCELLED appointments do not count toward revenue.

## Dashboard Counting Rules

- Today's appointments are counted by appointment_date in Asia/Ho_Chi_Minh.
- Revenue only includes COMPLETED appointments.
- Pending/cancelled/completed counts are filtered by business_id.
- Dashboard never reads across businesses.
