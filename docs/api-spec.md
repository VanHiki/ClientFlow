# ClientFlow API Spec

Base URL:

```text
http://localhost:8080
```

Default timezone:

```text
Asia/Ho_Chi_Minh
```

## Response Envelope

All API responses use the same envelope:

```json
{
  "code": 1000,
  "message": "Success message",
  "result": {}
}
```

Paginated endpoints return:

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
```

## Authentication

Protected endpoints require:

```http
Authorization: Bearer <token>
```

Public endpoints:

```text
/api/auth/**
/api/public/**
```

Owner endpoints require authority:

```text
ROLE_OWNER
```

## Enums

Appointment status:

```text
PENDING
CONFIRMED
CHECKED_IN
COMPLETED
CANCELLED
NO_SHOW
```

Business exception type:

```text
CLOSED_DAY
HOLIDAY
SPECIAL_CLOSURE
```

Day of week:

```text
MONDAY
TUESDAY
WEDNESDAY
THURSDAY
FRIDAY
SATURDAY
SUNDAY
```

## Auth

### Register Owner

```http
POST /api/auth/register
Content-Type: application/json
```

Request:

```json
{
  "fullName": "Hoang Khanh Van",
  "email": "owner@example.com",
  "password": "123456",
  "phone": "0909000000"
}
```

Response result:

```json
{
  "userId": 1,
  "fullName": "Hoang Khanh Van",
  "email": "owner@example.com",
  "role": "OWNER",
  "token": "<jwt>"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

Request:

```json
{
  "email": "owner@example.com",
  "password": "123456"
}
```

Response result:

```json
{
  "userId": 1,
  "fullName": "Hoang Khanh Van",
  "email": "owner@example.com",
  "role": "OWNER",
  "token": "<jwt>"
}
```

## Businesses

### Create Business

```http
POST /api/businesses
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "name": "Hiki Salon",
  "slug": "hiki-salon",
  "phone": "0909000000",
  "email": "salon@example.com",
  "address": "Ho Chi Minh City"
}
```

Response result:

```json
{
  "id": 1,
  "name": "Hiki Salon",
  "slug": "hiki-salon",
  "phone": "0909000000",
  "email": "salon@example.com",
  "address": "Ho Chi Minh City",
  "timezone": "Asia/Ho_Chi_Minh",
  "active": true
}
```

### List My Businesses

```http
GET /api/businesses?page=0&size=10
Authorization: Bearer <token>
```

Response result:

```json
{
  "content": [
    {
      "id": 1,
      "name": "Hiki Salon",
      "slug": "hiki-salon",
      "phone": "0909000000",
      "email": "salon@example.com",
      "address": "Ho Chi Minh City",
      "timezone": "Asia/Ho_Chi_Minh",
      "active": true
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### Get My Business

```http
GET /api/businesses/{businessId}
Authorization: Bearer <token>
```

### Update Business

```http
PUT /api/businesses/{businessId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "name": "Hiki Salon Updated",
  "slug": "hiki-salon-updated",
  "phone": "0909111222",
  "email": "salon.updated@example.com",
  "address": "District 1, Ho Chi Minh City"
}
```

Response result:

```json
{
  "id": 1,
  "name": "Hiki Salon Updated",
  "slug": "hiki-salon-updated",
  "phone": "0909111222",
  "email": "salon.updated@example.com",
  "address": "District 1, Ho Chi Minh City",
  "timezone": "Asia/Ho_Chi_Minh",
  "active": true
}
```

### Update Business Status

```http
PATCH /api/businesses/{businessId}/status
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "active": false
}
```

Response result:

```json
{
  "id": 1,
  "name": "Hiki Salon Updated",
  "slug": "hiki-salon-updated",
  "phone": "0909111222",
  "email": "salon.updated@example.com",
  "address": "District 1, Ho Chi Minh City",
  "timezone": "Asia/Ho_Chi_Minh",
  "active": false
}
```

## Services

### Create Service

```http
POST /api/businesses/{businessId}/services
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "name": "Hair Cut",
  "description": "Basic haircut",
  "price": 100000,
  "durationMinutes": 30
}
```

Response result:

```json
{
  "id": 1,
  "name": "Hair Cut",
  "description": "Basic haircut",
  "price": 100000.00,
  "durationMinutes": 30,
  "active": true
}
```

### List Services

```http
GET /api/businesses/{businessId}/services?page=0&size=10
Authorization: Bearer <token>
```

### Update Service

```http
PUT /api/businesses/{businessId}/services/{serviceId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "name": "Hair Cut Premium",
  "description": "Haircut with styling",
  "price": 150000,
  "durationMinutes": 45
}
```

Response result:

```json
{
  "id": 1,
  "name": "Hair Cut Premium",
  "description": "Haircut with styling",
  "price": 150000.00,
  "durationMinutes": 45,
  "active": true
}
```

### Update Service Status

```http
PATCH /api/businesses/{businessId}/services/{serviceId}/status
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "active": false
}
```

Response result:

```json
{
  "id": 1,
  "name": "Hair Cut Premium",
  "description": "Haircut with styling",
  "price": 150000.00,
  "durationMinutes": 45,
  "active": false
}
```

## Staff

### Create Staff

```http
POST /api/businesses/{businessId}/staff
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "fullName": "Nguyen Van A",
  "email": "staff1@example.com",
  "phone": "0909000001",
  "position": "Hair stylist"
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "userId": null,
  "fullName": "Nguyen Van A",
  "email": "staff1@example.com",
  "phone": "0909000001",
  "position": "Hair stylist",
  "active": true
}
```

### List Staff

```http
GET /api/businesses/{businessId}/staff?page=0&size=10
Authorization: Bearer <token>
```

### Update Staff

```http
PUT /api/businesses/{businessId}/staff/{staffId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "fullName": "Nguyen Van A Updated",
  "email": "staff.updated@example.com",
  "phone": "0909000011",
  "position": "Senior hair stylist"
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "userId": null,
  "fullName": "Nguyen Van A Updated",
  "email": "staff.updated@example.com",
  "phone": "0909000011",
  "position": "Senior hair stylist",
  "active": true
}
```

### Update Staff Status

```http
PATCH /api/businesses/{businessId}/staff/{staffId}/status
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "active": false
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "userId": null,
  "fullName": "Nguyen Van A Updated",
  "email": "staff.updated@example.com",
  "phone": "0909000011",
  "position": "Senior hair stylist",
  "active": false
}
```

## Staff Services

### Assign Service To Staff

```http
POST /api/businesses/{businessId}/staff/{staffId}/services/{serviceId}
Authorization: Bearer <token>
```

Response result:

```json
{
  "id": 1,
  "staffId": 1,
  "serviceId": 1,
  "serviceName": "Hair Cut",
  "price": 100000.00,
  "durationMinutes": 30
}
```

### List Staff Services

```http
GET /api/businesses/{businessId}/staff/{staffId}/services?page=0&size=10
Authorization: Bearer <token>
```

### Unassign Service From Staff

```http
DELETE /api/businesses/{businessId}/staff/{staffId}/services/{serviceId}
Authorization: Bearer <token>
```

The staff member will no longer appear in available slots for this service. Existing appointments are preserved.

## Working Hours

### Create Working Hour

```http
POST /api/businesses/{businessId}/staff/{staffId}/working-hours
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "dayOfWeek": "MONDAY",
  "startTime": "08:00",
  "endTime": "17:00"
}
```

Response result:

```json
{
  "id": 1,
  "staffId": 1,
  "dayOfWeek": "MONDAY",
  "startTime": "08:00:00",
  "endTime": "17:00:00",
  "active": true
}
```

### List Working Hours

```http
GET /api/businesses/{businessId}/staff/{staffId}/working-hours?page=0&size=10
Authorization: Bearer <token>
```

### Update Working Hour

```http
PUT /api/businesses/{businessId}/staff/{staffId}/working-hours/{workingHourId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "dayOfWeek": "MONDAY",
  "startTime": "09:00",
  "endTime": "18:00"
}
```

Response result:

```json
{
  "id": 1,
  "staffId": 1,
  "dayOfWeek": "MONDAY",
  "startTime": "09:00:00",
  "endTime": "18:00:00",
  "active": true
}
```

### Update Working Hour Status

```http
PATCH /api/businesses/{businessId}/staff/{staffId}/working-hours/{workingHourId}/status
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "active": false
}
```

Response result:

```json
{
  "id": 1,
  "staffId": 1,
  "dayOfWeek": "MONDAY",
  "startTime": "09:00:00",
  "endTime": "18:00:00",
  "active": false
}
```

## Staff Time Off

### Create Staff Time Off

```http
POST /api/businesses/{businessId}/staff/{staffId}/time-off
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "date": "2026-07-13",
  "startTime": "10:00",
  "endTime": "12:00",
  "reason": "Personal leave"
}
```

Response result:

```json
{
  "id": 1,
  "staffId": 1,
  "date": "2026-07-13",
  "startTime": "10:00:00",
  "endTime": "12:00:00",
  "reason": "Personal leave"
}
```

### List Staff Time Off

```http
GET /api/businesses/{businessId}/staff/{staffId}/time-off?page=0&size=10
Authorization: Bearer <token>
```

### Update Staff Time Off

```http
PUT /api/businesses/{businessId}/staff/{staffId}/time-off/{timeOffId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "date": "2026-07-13",
  "startTime": "13:00",
  "endTime": "15:00",
  "reason": "Updated personal leave"
}
```

The updated time range must not overlap another time-off record for the same staff member and date.

### Delete Staff Time Off

```http
DELETE /api/businesses/{businessId}/staff/{staffId}/time-off/{timeOffId}
Authorization: Bearer <token>
```

Deleting a time-off record opens its slots again. Existing appointments are not deleted.

## Business Exceptions

Business exceptions are full-day closures for a business. Available slots return an empty list for the date, and appointment creation is blocked.

### Create Business Exception

```http
POST /api/businesses/{businessId}/exceptions
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "date": "2026-07-13",
  "type": "HOLIDAY",
  "reason": "Public holiday"
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "date": "2026-07-13",
  "type": "HOLIDAY",
  "reason": "Public holiday"
}
```

### List Business Exceptions

```http
GET /api/businesses/{businessId}/exceptions?page=0&size=10
Authorization: Bearer <token>
```

### Update Business Exception

```http
PUT /api/businesses/{businessId}/exceptions/{exceptionId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "date": "2026-07-14",
  "type": "SPECIAL_CLOSURE",
  "reason": "Private event"
}
```

### Delete Business Exception

```http
DELETE /api/businesses/{businessId}/exceptions/{exceptionId}
Authorization: Bearer <token>
```

Deleting an exception opens the business's slots for that date again. Existing appointments are not deleted.

## Customers

### Create Customer

```http
POST /api/businesses/{businessId}/customers
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "fullName": "Tran Thi B",
  "phone": "0909000002",
  "email": "customer@example.com",
  "notes": "Regular customer"
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "fullName": "Tran Thi B",
  "phone": "0909000002",
  "email": "customer@example.com",
  "notes": "Regular customer",
  "active": true
}
```

### List Customers

```http
GET /api/businesses/{businessId}/customers?page=0&size=10
Authorization: Bearer <token>
```

### Update Customer

```http
PUT /api/businesses/{businessId}/customers/{customerId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "fullName": "Tran Thi B Updated",
  "phone": "0909000022",
  "email": "customer.updated@example.com",
  "notes": "Prefers afternoon appointments"
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "fullName": "Tran Thi B Updated",
  "phone": "0909000022",
  "email": "customer.updated@example.com",
  "notes": "Prefers afternoon appointments",
  "active": true
}
```

### Update Customer Status

```http
PATCH /api/businesses/{businessId}/customers/{customerId}/status
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "active": false
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "fullName": "Tran Thi B Updated",
  "phone": "0909000022",
  "email": "customer.updated@example.com",
  "notes": "Prefers afternoon appointments",
  "active": false
}
```

## Availability

### Owner Available Slots

```http
GET /api/businesses/{businessId}/available-slots?serviceId={serviceId}&date=2026-07-13
Authorization: Bearer <token>
```

Response result:

```json
[
  {
    "staffId": 1,
    "staffName": "Nguyen Van A",
    "date": "2026-07-13",
    "startTime": "08:00:00",
    "endTime": "08:30:00"
  }
]
```

Slot rules:

```text
Service must be active.
Staff must be assigned to service.
Slot must fit staff working hours.
Slot must not overlap blocking appointments.
Slot must not overlap staff time off.
Slot date must not be a business exception date.
Past slots are excluded.
```

## Appointments

### Create Appointment As Owner

```http
POST /api/businesses/{businessId}/appointments
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "customerId": 1,
  "serviceId": 1,
  "staffId": 1,
  "appointmentDate": "2026-07-13",
  "startTime": "08:00",
  "note": "Owner booking"
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "customerId": 1,
  "serviceId": 1,
  "staffId": 1,
  "appointmentDate": "2026-07-13",
  "startTime": "08:00:00",
  "endTime": "08:30:00",
  "status": "PENDING",
  "timezone": "Asia/Ho_Chi_Minh",
  "note": "Owner booking"
}
```

### List Appointments

```http
GET /api/businesses/{businessId}/appointments?page=0&size=10
Authorization: Bearer <token>
```

### Update Appointment Status

```http
PATCH /api/businesses/{businessId}/appointments/{appointmentId}/status
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "status": "CONFIRMED"
}
```

Allowed transitions:

```text
PENDING -> CONFIRMED
PENDING -> CANCELLED
CONFIRMED -> CHECKED_IN
CONFIRMED -> COMPLETED
CONFIRMED -> CANCELLED
CONFIRMED -> NO_SHOW
CHECKED_IN -> COMPLETED
CHECKED_IN -> CANCELLED
```

Final statuses:

```text
COMPLETED
CANCELLED
NO_SHOW
```

## Appointment Notes

Appointment notes are internal owner notes for an appointment.

### Create Appointment Note

```http
POST /api/businesses/{businessId}/appointments/{appointmentId}/notes
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "content": "Customer prefers a quiet stylist and short wait time."
}
```

Response result:

```json
{
  "id": 1,
  "appointmentId": 1,
  "authorUserId": 1,
  "authorName": "Hoang Khanh Van",
  "content": "Customer prefers a quiet stylist and short wait time.",
  "createdAt": "2026-07-10T09:30:00"
}
```

### List Appointment Notes

```http
GET /api/businesses/{businessId}/appointments/{appointmentId}/notes?page=0&size=10
Authorization: Bearer <token>
```

Response result:

```json
{
  "content": [
    {
      "id": 1,
      "appointmentId": 1,
      "authorUserId": 1,
      "authorName": "Hoang Khanh Van",
      "content": "Customer prefers a quiet stylist and short wait time.",
      "createdAt": "2026-07-10T09:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### Update Appointment Note

```http
PUT /api/businesses/{businessId}/appointments/{appointmentId}/notes/{noteId}
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "content": "Customer prefers a quiet stylist."
}
```

### Delete Appointment Note

```http
DELETE /api/businesses/{businessId}/appointments/{appointmentId}/notes/{noteId}
Authorization: Bearer <token>
```

## Dashboard

### Get Dashboard

```http
GET /api/businesses/{businessId}/dashboard
Authorization: Bearer <token>
```

Response result:

```json
{
  "totalCustomers": 3,
  "totalServices": 2,
  "totalStaff": 1,
  "totalAppointments": 5,
  "todayAppointments": 1,
  "pendingAppointments": 2,
  "confirmedAppointments": 1,
  "completedAppointments": 1,
  "upcomingAppointments": [
    {
      "id": 1,
      "customerName": "Tran Thi B",
      "serviceName": "Hair Cut",
      "staffName": "Nguyen Van A",
      "appointmentDate": "2026-07-13",
      "startTime": "08:00:00",
      "endTime": "08:30:00",
      "status": "PENDING"
    }
  ]
}
```

## Public Booking

Public booking endpoints do not require authentication.

### Get Public Business

```http
GET /api/public/businesses/{slug}
```

Response result:

```json
{
  "id": 1,
  "name": "Hiki Salon",
  "slug": "hiki-salon",
  "phone": "0909000000",
  "email": "salon@example.com",
  "address": "Ho Chi Minh City",
  "timezone": "Asia/Ho_Chi_Minh"
}
```

### Get Public Services

```http
GET /api/public/businesses/{slug}/services
```

Response result:

```json
[
  {
    "id": 1,
    "name": "Hair Cut",
    "description": "Basic haircut",
    "price": 100000.00,
    "durationMinutes": 30
  }
]
```

### Get Public Available Slots

```http
GET /api/public/businesses/{slug}/available-slots?serviceId={serviceId}&date=2026-07-13
```

Response result:

```json
[
  {
    "staffId": 1,
    "staffName": "Nguyen Van A",
    "date": "2026-07-13",
    "startTime": "08:00:00",
    "endTime": "08:30:00"
  }
]
```

### Public Create Appointment

```http
POST /api/public/businesses/{slug}/appointments
Content-Type: application/json
```

Request:

```json
{
  "customerFullName": "Tran Thi B",
  "customerPhone": "0909000002",
  "customerEmail": "customer@example.com",
  "serviceId": 1,
  "staffId": 1,
  "appointmentDate": "2026-07-13",
  "startTime": "08:00",
  "note": "Public booking"
}
```

Response result:

```json
{
  "id": 1,
  "businessId": 1,
  "customerId": 1,
  "serviceId": 1,
  "staffId": 1,
  "appointmentDate": "2026-07-13",
  "startTime": "08:00:00",
  "endTime": "08:30:00",
  "status": "PENDING",
  "timezone": "Asia/Ho_Chi_Minh",
  "note": "Public booking"
}
```

## End-To-End Test Order

Use this order when testing in Postman:

```text
1. POST /api/auth/register
2. POST /api/auth/login
3. POST /api/businesses
4. POST /api/businesses/{businessId}/services
5. POST /api/businesses/{businessId}/staff
6. POST /api/businesses/{businessId}/staff/{staffId}/services/{serviceId}
7. POST /api/businesses/{businessId}/staff/{staffId}/working-hours
8. GET /api/businesses/{businessId}/available-slots
9. POST /api/public/businesses/{slug}/appointments
10. PATCH /api/businesses/{businessId}/appointments/{appointmentId}/status
11. POST /api/businesses/{businessId}/appointments/{appointmentId}/notes
12. POST /api/businesses/{businessId}/staff/{staffId}/time-off
13. POST /api/businesses/{businessId}/exceptions
14. GET /api/businesses/{businessId}/dashboard
```

Important negative tests:

```text
Duplicate business slug -> Business slug already exists
Duplicate service name in business -> Service name already exists
Overlapping working hours -> Working hour overlaps with existing working hour
Working hour not found -> Working hour not found
Staff not assigned to service -> Staff is not assigned to this service
Staff is inactive -> Staff is inactive
Customer is inactive when owner creates appointment -> Customer is inactive
Appointment in the past -> Cannot book appointment in the past
Appointment outside working hours -> Appointment is outside staff working hours
Appointment overlaps another blocking appointment -> Staff already has an appointment in this time range
Appointment overlaps staff time off -> Appointment is during staff time off
Appointment date is business exception -> Appointment date is closed for this business
Invalid appointment status transition -> Invalid appointment status transition
```
