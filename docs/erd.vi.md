# Thiết kế Cơ sở dữ liệu (ERD) - ClientFlow MVP

> Phiên bản: MVP

Tài liệu này mô tả thiết kế cơ sở dữ liệu đầu tiên của hệ thống **ClientFlow**.

Mục tiêu là xây dựng một hệ thống hỗ trợ:

- Đăng nhập và phân quyền.
- Quản lý Business.
- Quản lý dịch vụ.
- Quản lý nhân viên.
- Quản lý lịch làm việc.
- Đặt lịch trực tuyến.
- Quản lý khách hàng.
- Theo dõi lịch hẹn.
- Thông báo trong hệ thống.
- Quên mật khẩu.

---

# 1. Phạm vi của MVP

## Bao gồm

Hệ thống sẽ có các bảng sau:

- roles
- users
- businesses
- services
- staff_profiles
- staff_services
- working_hours
- business_exceptions
- staff_time_off
- customers
- appointments
- appointment_notes
- notifications
- password_reset_tokens

---

## Chưa triển khai trong MVP

Các chức năng dưới đây sẽ được bổ sung ở các phiên bản sau:

- payments
- subscription_plans
- branches
- invoices
- audit_logs

---

# 2. Quan hệ giữa các bảng (ERD)

Quan hệ chính của hệ thống:

```
Role
    │
    └───────────────< User

User
    ├───────────────< Business
    ├───────────────< Appointment Note
    ├───────────────< Notification
    └───────────────< Password Reset Token

Business
    ├───────────────< Service
    ├───────────────< Staff Profile
    ├───────────────< Customer
    ├───────────────< Appointment
    └───────────────< Business Exception

Staff Profile
    ├───────────────< Working Hour
    ├───────────────< Staff Time Off
    ├───────────────< Appointment
    └──────< Staff Service >────── Service

Customer
    └───────────────< Appointment

Appointment
    └───────────────< Appointment Note
```

Có thể hiểu đơn giản như sau:

- Một User có thể sở hữu một Business.
- Một Business có nhiều Service.
- Một Business có nhiều Staff.
- Một Staff có thể làm nhiều Service.
- Một Service cũng có thể do nhiều Staff thực hiện.
- Khách hàng đặt Appointment.
- Appointment sẽ gắn với:
    - Customer
    - Staff
    - Service
    - Business

---

# 3. Bảng roles

## Mục đích

Lưu danh sách các vai trò của hệ thống.

Ví dụ:

```
OWNER
STAFF
CUSTOMER
ADMIN
```

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| name | Tên Role |
| description | Mô tả |
| created_at | Thời gian tạo |
| updated_at | Thời gian cập nhật |

---

## Lưu ý

- name phải là duy nhất.
- Spring Security sẽ dựa vào bảng này để phân quyền.

---

# 4. Bảng users

## Mục đích

Lưu tài khoản đăng nhập của người dùng.

Đây là bảng trung tâm của hệ thống.

Ví dụ:

- Chủ Spa
- Nhân viên
- Quản trị viên

đều là User.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| role_id | Vai trò |
| full_name | Họ tên |
| email | Email đăng nhập |
| password_hash | Mật khẩu đã mã hóa |
| phone | Số điện thoại |
| enabled | Có đang hoạt động không |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Quy tắc

- Email phải duy nhất.
- Email nên được chuyển về chữ thường trước khi lưu.
- Password luôn được mã hóa bằng BCrypt.
- Không bao giờ lưu mật khẩu gốc.

---

## Chỉ mục (Index)

Nên tạo:

```
UNIQUE(email)

INDEX(role_id)
```

để tăng tốc tìm kiếm.

---

# 5. Bảng businesses

## Mục đích

Mỗi Business đại diện cho một doanh nghiệp sử dụng ClientFlow.

Ví dụ:

- Salon tóc
- Spa
- Phòng khám
- Tiệm nail

Mỗi Business sẽ có dữ liệu riêng.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| owner_id | Chủ Business |
| name | Tên doanh nghiệp |
| slug | Đường dẫn Booking |
| phone | Điện thoại |
| email | Email |
| address | Địa chỉ |
| timezone | Múi giờ |
| active | Có đang hoạt động không |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Ví dụ

```
Tên:

ABC Spa

Slug:

abc-spa
```

Khách hàng sẽ truy cập:

```
/booking/abc-spa
```

để đặt lịch.

---

## Quy tắc

- Slug phải duy nhất.
- Một Owner có thể sở hữu nhiều Business.
- Mọi dữ liệu của Business đều phải được tách biệt bằng `business_id`.

Ví dụ:

Business A

không được phép xem:

- lịch hẹn
- khách hàng
- dịch vụ

của Business B.

---

## Chỉ mục

```
UNIQUE(slug)

INDEX(owner_id)
```

giúp:

- tìm Business theo slug nhanh hơn.
- lấy danh sách Business của Owner nhanh hơn.

---

# 6. Bảng services

## Mục đích

Lưu danh sách các dịch vụ mà Business cung cấp.

Ví dụ:

- Cắt tóc nam
- Gội đầu
- Massage
- Chăm sóc da
- Khám tổng quát

Khách hàng sẽ chọn một trong các dịch vụ này khi đặt lịch.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| business_id | Business sở hữu dịch vụ |
| name | Tên dịch vụ |
| description | Mô tả |
| price | Giá dịch vụ |
| duration_minutes | Thời lượng (phút) |
| active | Có đang mở bán không |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Ví dụ

```
Tên dịch vụ:

Massage Body

Giá:

500.000 VNĐ

Thời lượng:

90 phút
```

---

## Quy tắc

- Một Business có thể có nhiều Service.
- Giá dịch vụ không được âm.
- Thời lượng phải lớn hơn 0.
- Nếu Service bị tắt (`active = false`) thì:
    - không hiển thị cho khách hàng.
    - không thể đặt lịch mới.
- Không xóa cứng Service nếu đã có Appointment.

---

## Chỉ mục

```
INDEX(business_id)

INDEX(business_id, active)
```

---

# 7. Bảng staff_profiles

## Mục đích

Lưu thông tin nhân viên làm việc trong Business.

Lưu ý:

Đây **không phải tài khoản đăng nhập**.

Đây là hồ sơ nhân viên.

Một nhân viên:

- có thể có tài khoản đăng nhập
- hoặc chỉ tồn tại để quản lý lịch làm việc.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| business_id | Business quản lý |
| user_id | Liên kết với tài khoản User (nếu có) |
| display_name | Tên hiển thị |
| email | Email |
| phone | Số điện thoại |
| active | Có đang làm việc không |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Ví dụ

```
Nguyễn Văn A

Chuyên viên Massage
```

hoặc

```
Lê Thị B

Stylist
```

---

## Quy tắc

- Một Business có nhiều Staff.
- Một Staff chỉ thuộc một Business.
- user_id có thể để trống.

Ví dụ:

Chủ Spa tạo nhân viên trước.

Sau này mới cấp tài khoản.

Khi đó:

```
user_id = NULL
```

Sau khi nhân viên đăng ký:

```
user_id = 15
```

---

## Chỉ mục

```
INDEX(business_id)

UNIQUE(user_id)
```

---

# 8. Bảng staff_services

## Mục đích

Đây là bảng trung gian giữa:

- Staff
- Service

Vì:

Một Staff có thể làm nhiều Service.

Một Service cũng có thể do nhiều Staff thực hiện.

Đây là quan hệ **Many-to-Many**.

---

## Ví dụ

```
Nhân viên A

↓

Massage

↓

Chăm sóc da
```

Nhân viên A làm được hai dịch vụ.

---

```
Massage

↓

Nhân viên A

↓

Nhân viên B

↓

Nhân viên C
```

Dịch vụ Massage có ba nhân viên thực hiện.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| staff_id | Nhân viên |
| service_id | Dịch vụ |
| created_at | Ngày tạo |

---

## Khóa chính

Sử dụng khóa chính kép:

```
PRIMARY KEY

(staff_id, service_id)
```

để tránh việc:

```
A

↓

Massage
```

bị thêm nhiều lần.

---

## Chỉ mục

```
INDEX(service_id)
```

---

# 9. Bảng working_hours

## Mục đích

Lưu lịch làm việc cố định hằng tuần của từng nhân viên.

Ví dụ:

```
Thứ Hai

09:00 → 18:00
```

```
Thứ Ba

08:00 → 17:00
```

...

Hệ thống sẽ dựa vào bảng này để sinh các Slot đặt lịch.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| staff_id | Nhân viên |
| day_of_week | Thứ trong tuần |
| start_time | Giờ bắt đầu |
| end_time | Giờ kết thúc |
| active | Có đang áp dụng không |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Quy ước day_of_week

```
1 = Monday

2 = Tuesday

3 = Wednesday

4 = Thursday

5 = Friday

6 = Saturday

7 = Sunday
```

---

## Ví dụ

```
Staff:

Nguyễn Văn A

Monday

09:00 → 17:00
```

```
Tuesday

08:30 → 16:30
```

---

## Quy tắc

- end_time phải lớn hơn start_time.
- Một nhân viên có tối đa một lịch làm việc cho mỗi ngày trong tuần.
- Nếu active = false thì lịch đó sẽ không được sử dụng để sinh Slot.

---

## Chỉ mục

```
INDEX

(staff_id, day_of_week, active)
```

để tăng tốc khi tìm lịch làm việc theo:

- nhân viên
- thứ trong tuần.

---

## Luồng sinh Slot

Khi khách chọn:

```
Massage

↓

Ngày 20/07/2026
```

Backend sẽ:

1. Tìm các Staff làm được Massage.
2. Lấy Working Hours của từng Staff.
3. Kiểm tra Business có nghỉ không.
4. Kiểm tra Staff có xin nghỉ không.
5. Sinh các Slot 30 phút.
6. Loại bỏ Slot đã bị đặt.
7. Trả về danh sách Slot còn trống.

# 10. Bảng business_exceptions

## Mục đích

Lưu các ngày Business không hoạt động.

Đây là những ngày mà **toàn bộ Business sẽ ngừng nhận lịch**, kể cả khi nhân viên vẫn có lịch làm việc.

Ví dụ:

- Nghỉ Tết
- Nghỉ lễ Quốc khánh
- Đóng cửa để bảo trì
- Nghỉ vì sự kiện nội bộ

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| business_id | Business sở hữu |
| exception_date | Ngày nghỉ |
| type | Loại ngày nghỉ |
| reason | Lý do nghỉ |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Các loại ngày nghỉ

```
CLOSED_DAY

HOLIDAY

SPECIAL_CLOSURE
```

Ý nghĩa:

- **CLOSED_DAY:** Ngày nghỉ định kỳ.
- **HOLIDAY:** Nghỉ lễ, nghỉ Tết.
- **SPECIAL_CLOSURE:** Nghỉ đột xuất như bảo trì hoặc sự kiện.

---

## Ví dụ

```
Ngày:

02/09/2026

Loại:

HOLIDAY

Lý do:

Quốc khánh
```

---

## Quy tắc

Business nghỉ thì:

- Không sinh Slot.
- Không cho đặt lịch.
- Không phân biệt Staff nào.

Nói cách khác:

Business nghỉ ⇒ Tất cả Staff đều nghỉ.

---

## Chỉ mục

```
UNIQUE

(business_id, exception_date)
```

Một Business chỉ được khai báo một lần cho mỗi ngày.

Ngoài ra:

```
INDEX

(business_id, exception_date)
```

để tăng tốc tìm kiếm.

---

# 11. Bảng staff_time_off

## Mục đích

Lưu thời gian nghỉ riêng của từng nhân viên.

Khác với bảng Business Exception.

Ví dụ:

Business vẫn mở cửa.

Nhưng:

```
Nguyễn Văn A

↓

Xin nghỉ phép
```

thì chỉ riêng nhân viên đó không nhận lịch.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| staff_id | Nhân viên |
| time_off_date | Ngày nghỉ |
| start_time | Giờ bắt đầu nghỉ |
| end_time | Giờ kết thúc nghỉ |
| reason | Lý do |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Ví dụ 1

```
20/07/2026

09:00

↓

12:00
```

Nhân viên chỉ nghỉ buổi sáng.

Buổi chiều vẫn có thể nhận lịch.

---

## Ví dụ 2

```
20/07/2026

start_time = NULL

end_time = NULL
```

Có nghĩa:

Nghỉ cả ngày.

---

## Quy tắc

Nếu:

```
start_time

và

end_time

đều NULL
```

⇒ Nghỉ cả ngày.

Nếu có giờ:

```
09:00

↓

12:00
```

⇒ Chỉ nghỉ trong khoảng đó.

---

## Khi sinh Slot

Backend sẽ:

1. Lấy lịch làm việc.
2. Kiểm tra Staff Time Off.
3. Loại bỏ những Slot nằm trong khoảng nghỉ.

Ví dụ:

```
Ca làm

09:00 → 17:00
```

```
Xin nghỉ

13:00 → 15:00
```

Slot:

```
13:00

13:30

14:00

14:30
```

sẽ không được sinh.

---

## Chỉ mục

```
INDEX

(staff_id, time_off_date)
```

---

# 12. Bảng customers

## Mục đích

Lưu thông tin khách hàng của từng Business.

Lưu ý:

Khách hàng **không cần có tài khoản đăng nhập**.

Họ chỉ cần:

- Họ tên
- Số điện thoại
- Email (nếu có)

là có thể đặt lịch.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| business_id | Business quản lý |
| full_name | Họ tên |
| email | Email |
| phone | Số điện thoại |
| notes | Ghi chú |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Ví dụ

```
Nguyễn Văn Bình

0901234567

binh@gmail.com
```

---

## Quy tắc

Khách hàng được quản lý theo từng Business.

Ví dụ:

```
ABC Spa

↓

Nguyễn Văn Bình
```

và

```
XYZ Salon

↓

Nguyễn Văn Bình
```

được xem là **hai Customer khác nhau**.

Điều này giúp:

- Mỗi Business quản lý khách hàng độc lập.
- Không chia sẻ dữ liệu khách hàng giữa các Business.

---

## Khi khách đặt lịch

Nếu tìm thấy khách theo:

- Business
- Số điện thoại (hoặc Email)

⇒ Sử dụng Customer hiện có.

Nếu không tìm thấy:

⇒ Tự động tạo Customer mới.

---

## Chỉ mục

```
INDEX(business_id)
```

```
INDEX(business_id, phone)
```

```
INDEX(business_id, email)
```

Các chỉ mục này giúp:

- Tìm khách theo số điện thoại nhanh hơn.
- Tìm khách theo email nhanh hơn.
- Không phải quét toàn bộ bảng Customer.

---

# Mối quan hệ của ba bảng

```
Business
    │
    ├───────────────< Business Exception
    │
    ├───────────────< Customer
    │
    └───────────────< Staff

Staff
    │
    └───────────────< Staff Time Off
```

Có thể hiểu đơn giản:

- **Business Exception**: Cửa hàng nghỉ.
- **Staff Time Off**: Nhân viên nghỉ.
- **Customer**: Khách hàng của Business.

Khi sinh Slot, Backend sẽ kiểm tra theo thứ tự:

1. Business có nghỉ không?
2. Staff có làm dịch vụ đó không?
3. Staff có lịch làm việc không?
4. Staff có xin nghỉ không?
5. Slot có bị trùng Appointment không?
6. Nếu tất cả đều hợp lệ thì trả về Slot khả dụng.

# 13. Bảng appointments

## Mục đích

Lưu toàn bộ lịch hẹn trong hệ thống.

Đây là bảng quan trọng nhất của ClientFlow.

Mỗi lần khách hàng đặt lịch thành công, hệ thống sẽ tạo một bản ghi trong bảng này.

---

## Appointment dùng để làm gì?

Bảng này dùng để lưu:

- Khách hàng nào đặt lịch.
- Đặt ở Business nào.
- Chọn Service nào.
- Chọn Staff nào.
- Ngày giờ bắt đầu.
- Ngày giờ kết thúc.
- Trạng thái hiện tại của lịch.
- Ghi chú của khách hàng.
- Lý do hủy nếu có.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| business_id | Business nhận lịch |
| customer_id | Khách hàng đặt lịch |
| service_id | Dịch vụ được đặt |
| staff_id | Nhân viên phụ trách |
| booking_code | Mã đặt lịch |
| appointment_date | Ngày hẹn |
| start_time | Giờ bắt đầu |
| end_time | Giờ kết thúc |
| status | Trạng thái lịch |
| customer_note | Ghi chú từ khách hàng |
| cancel_reason | Lý do hủy |
| completed_at | Thời điểm hoàn thành |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Ví dụ

```text
Business:

ABC Spa

Customer:

Nguyễn Văn Bình

Service:

Massage Body

Staff:

Nguyễn Văn A

Ngày:

20/07/2026

Giờ:

09:00 → 10:30

Status:

PENDING
```

---

## booking_code

`booking_code` là mã đặt lịch công khai.

Khách hàng có thể dùng mã này để:

- tra cứu lịch hẹn.
- yêu cầu hủy lịch.
- xác nhận thông tin đặt lịch.

Ví dụ:

```text
CF-20260720-8X92
```

Quy tắc:

- Bắt buộc có.
- Không được trùng.
- Nên đủ ngắn để khách dễ đọc.
- Không nên dùng trực tiếp `id` của bảng.

---

## appointment_date, start_time, end_time

Hệ thống lưu ngày và giờ tách riêng:

```text
appointment_date

start_time

end_time
```

Ví dụ:

```text
appointment_date = 2026-07-20

start_time = 09:00

end_time = 10:30
```

Lý do:

- Dễ lọc lịch theo ngày.
- Dễ kiểm tra trùng giờ.
- Dễ hiển thị lịch làm việc.
- Phù hợp với MVP dùng một timezone cố định.

---

## status

Trạng thái lịch hẹn.

Các giá trị gồm:

```text
PENDING

CONFIRMED

CHECKED_IN

COMPLETED

CANCELLED

NO_SHOW
```

Ý nghĩa:

- **PENDING:** Khách vừa đặt lịch, đang chờ xác nhận.
- **CONFIRMED:** Lịch đã được xác nhận.
- **CHECKED_IN:** Khách đã đến.
- **COMPLETED:** Dịch vụ đã hoàn thành.
- **CANCELLED:** Lịch đã bị hủy.
- **NO_SHOW:** Khách không đến.

---

## Luồng trạng thái

```text
PENDING
    ↓
CONFIRMED
    ↓
CHECKED_IN
    ↓
COMPLETED
```

Các luồng phụ:

```text
PENDING → CANCELLED

CONFIRMED → CANCELLED

CONFIRMED → NO_SHOW

CHECKED_IN → CANCELLED
```

Sau khi lịch đã là:

- COMPLETED
- CANCELLED
- NO_SHOW

thì không được đổi trạng thái nữa.

---

## Quy tắc chiếm Slot

Một Appointment sẽ chiếm Slot nếu trạng thái là:

```text
PENDING

CONFIRMED

CHECKED_IN
```

Một Appointment sẽ không chiếm Slot nếu trạng thái là:

```text
CANCELLED

NO_SHOW

COMPLETED
```

Ví dụ:

Nếu Staff A đã có lịch:

```text
09:00 → 10:00

Status: CONFIRMED
```

thì khách khác không thể đặt Staff A trong khoảng:

```text
09:30 → 10:30
```

---

## Quy tắc kiểm tra trùng lịch

Hai lịch bị xem là trùng nếu thỏa tất cả điều kiện:

```text
same staff_id

same appointment_date

existing status in PENDING, CONFIRMED, CHECKED_IN

new_start < existing_end

new_end > existing_start
```

---

## Ví dụ trùng lịch

Lịch đã có:

```text
09:00 → 10:00
```

Các lịch sau bị trùng:

```text
09:30 → 10:30

08:30 → 09:30
```

Các lịch sau không trùng:

```text
10:00 → 11:00

08:00 → 09:00
```

---

## Quy tắc khi tạo Appointment

Khi tạo lịch mới, Backend bắt buộc phải:

1. Tìm Business theo `business_id` hoặc `businessSlug`.
2. Kiểm tra Business đang active.
3. Tìm Service thuộc đúng Business.
4. Kiểm tra Service đang active.
5. Tìm Staff thuộc đúng Business.
6. Kiểm tra Staff đang active.
7. Kiểm tra Staff có làm Service đó không.
8. Kiểm tra ngày đặt không nằm trong quá khứ.
9. Kiểm tra giờ đặt không nằm trong quá khứ nếu là hôm nay.
10. Kiểm tra Slot nằm trong Working Hours.
11. Kiểm tra Slot không rơi vào Staff Time Off.
12. Kiểm tra ngày đó Business không nghỉ.
13. Kiểm tra trùng lịch trong Transaction.
14. Nếu hợp lệ thì tạo Appointment với status `PENDING`.

---

## Vì sao phải kiểm tra lại trong Transaction?

API Available Slots chỉ cho biết Slot còn trống tại thời điểm gọi API.

Nhưng có thể xảy ra trường hợp:

```text
User A xem slot 09:00 còn trống.

User B cũng xem slot 09:00 còn trống.

Cả hai cùng bấm đặt lịch.
```

Nếu Backend không kiểm tra lại khi tạo Appointment thì cả hai đều đặt thành công.

Điều này sai.

Vì vậy:

- Khi tạo Appointment phải kiểm tra trùng lại.
- Việc kiểm tra nên nằm trong Transaction.
- Nếu hai request đặt cùng một Slot, chỉ một request được thành công.

---

## Chỉ mục

Nên tạo các index sau:

```text
UNIQUE(booking_code)
```

Để đảm bảo mã booking không trùng.

```text
INDEX(business_id, appointment_date)
```

Để lấy lịch của một Business theo ngày.

```text
INDEX(staff_id, appointment_date, start_time, end_time)
```

Để kiểm tra trùng lịch nhanh hơn.

```text
INDEX(business_id, status)
```

Để lọc lịch theo trạng thái.

```text
INDEX(customer_id)
```

Để xem lịch sử đặt lịch của khách hàng.

```text
INDEX(service_id)
```

Để thống kê lịch theo dịch vụ.

---

## Ghi chú quan trọng

Bảng `appointments` là trung tâm của các chức năng:

- Đặt lịch công khai.
- Quản lý lịch của Owner.
- Quản lý lịch của Staff.
- Lịch sử khách hàng.
- Thống kê Dashboard.
- Notification.
- Appointment Notes.

---

# 14. Bảng appointment_notes

## Mục đích

Lưu ghi chú nội bộ cho một Appointment.

Ghi chú này thường do:

- Owner
- Staff

thêm vào trong quá trình chăm sóc khách hàng.

---

## Ví dụ

```text
Khách yêu cầu phòng riêng.

Khách bị dị ứng tinh dầu.

Khách muốn đổi sang nhân viên nữ.

Khách đã gọi điện xác nhận.
```

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| appointment_id | Lịch hẹn |
| author_id | Người viết ghi chú |
| note | Nội dung ghi chú |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Quy tắc

- Một Appointment có thể có nhiều Note.
- Một Note chỉ thuộc một Appointment.
- Người viết Note là một User trong hệ thống.
- Khách hàng không nhìn thấy ghi chú nội bộ này.

---

## Ví dụ quan hệ

```text
Appointment #1001

↓

Note 1:

Khách muốn tư vấn thêm trước khi làm dịch vụ.

↓

Note 2:

Đã gọi xác nhận lúc 18:30.
```

---

## Chỉ mục

```text
INDEX(appointment_id)
```

Dùng để lấy danh sách Note của một Appointment.

```text
INDEX(author_id)
```

Dùng để xem các Note do một User đã tạo.

---

# Mối quan hệ giữa Appointment và các bảng khác

```text
Business
    │
    └───────────────< Appointment

Customer
    │
    └───────────────< Appointment

Service
    │
    └───────────────< Appointment

Staff Profile
    │
    └───────────────< Appointment

Appointment
    │
    └───────────────< Appointment Note

User
    │
    └───────────────< Appointment Note
```

Có thể hiểu:

- Business nhận lịch.
- Customer là người đặt lịch.
- Service là dịch vụ được chọn.
- Staff là người thực hiện dịch vụ.
- Appointment Note là ghi chú nội bộ cho lịch đó.
- User là người tạo ghi chú.

---

# Ví dụ hoàn chỉnh

Một khách đặt lịch:

```text
Khách hàng:

Nguyễn Văn Bình

Business:

ABC Spa

Dịch vụ:

Massage Body

Nhân viên:

Nguyễn Văn A

Ngày:

20/07/2026

Giờ:

09:00 → 10:30

Ghi chú khách hàng:

Tôi muốn phòng yên tĩnh.
```

Hệ thống sẽ tạo:

```text
appointments
```

với:

```text
status = PENDING

booking_code = CF-20260720-8X92
```

Sau đó Owner xác nhận:

```text
status = CONFIRMED
```

Khi khách đến:

```text
status = CHECKED_IN
```

Khi làm xong dịch vụ:

```text
status = COMPLETED
```

# 15. Bảng notifications

## Mục đích

Lưu các thông báo trong hệ thống.

Trong phiên bản MVP, Notification chỉ được lưu trong cơ sở dữ liệu để hiển thị trên giao diện.

Hệ thống **chưa gửi Email hoặc SMS**.

---

## Notification dùng để làm gì?

Thông báo giúp:

- Chủ Business biết có khách đặt lịch mới.
- Nhân viên biết mình được phân công lịch hẹn.
- Người dùng biết lịch đã được xác nhận.
- Nhắc lịch sắp diễn ra.
- Thông báo đặt lại mật khẩu.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| business_id | Business liên quan |
| recipient_user_id | Người nhận thông báo |
| appointment_id | Lịch hẹn liên quan (nếu có) |
| type | Loại thông báo |
| title | Tiêu đề |
| message | Nội dung |
| read_at | Thời điểm đã đọc |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Các loại Notification

```
APPOINTMENT_CREATED

APPOINTMENT_CONFIRMED

APPOINTMENT_CANCELLED

APPOINTMENT_COMPLETED

APPOINTMENT_REMINDER

PASSWORD_RESET_REQUESTED
```

---

## Ý nghĩa

| Type | Mô tả |
|------|--------|
| APPOINTMENT_CREATED | Có lịch hẹn mới |
| APPOINTMENT_CONFIRMED | Lịch đã được xác nhận |
| APPOINTMENT_CANCELLED | Lịch bị hủy |
| APPOINTMENT_COMPLETED | Dịch vụ đã hoàn thành |
| APPOINTMENT_REMINDER | Nhắc lịch sắp diễn ra |
| PASSWORD_RESET_REQUESTED | Người dùng yêu cầu đặt lại mật khẩu |

---

## Ví dụ

```
Title:

Có lịch hẹn mới

Message:

Nguyễn Văn Bình vừa đặt lịch Massage Body lúc 09:00 ngày 20/07.
```

---

## Quy tắc

Nếu:

```
read_at = NULL
```

⇒ Thông báo chưa đọc.

Nếu:

```
read_at

có giá trị
```

⇒ Thông báo đã được đọc.

---

## Chỉ mục

```
INDEX(business_id, created_at)
```

Dùng để lấy Notification mới nhất của Business.

```
INDEX(recipient_user_id, read_at)
```

Dùng để lấy Notification chưa đọc của User.

```
INDEX(appointment_id)
```

Dùng để tìm Notification liên quan đến một Appointment.

---

# 16. Bảng password_reset_tokens

## Mục đích

Lưu Token dùng để đặt lại mật khẩu.

Khi người dùng chọn:

```
Quên mật khẩu
```

hệ thống sẽ tạo một Token mới và lưu vào bảng này.

---

## Các cột

| Cột | Ý nghĩa |
|------|----------|
| id | Khóa chính |
| user_id | Người yêu cầu |
| token_hash | Token đã mã hóa |
| expires_at | Thời điểm hết hạn |
| used_at | Thời điểm đã sử dụng |
| created_at | Ngày tạo |
| updated_at | Ngày cập nhật |

---

## Quy trình

### Bước 1

Người dùng nhập Email.

↓

### Bước 2

Hệ thống sinh Token.

↓

### Bước 3

Token được Hash.

↓

### Bước 4

Lưu vào Database.

↓

### Bước 5

Gửi Link Reset Password.

↓

### Bước 6

Người dùng đổi mật khẩu.

↓

### Bước 7

Ghi thời gian vào:

```
used_at
```

để Token không thể dùng lại.

---

## Quy tắc

- Không lưu Token gốc.
- Chỉ lưu Token sau khi Hash.
- Token chỉ dùng một lần.
- Token hết hạn sau 15 phút.
- Nếu Token đã dùng hoặc hết hạn thì từ chối.

---

## Chỉ mục

```
UNIQUE(token_hash)
```

Đảm bảo không có hai Token giống nhau.

```
INDEX(user_id)
```

Để lấy Token của User.

```
INDEX(expires_at)
```

Để dọn các Token hết hạn.

---

# 17. Quy tắc phân tách dữ liệu (Data Isolation Rules)

ClientFlow là hệ thống **đa doanh nghiệp (Multi-tenant)**.

Điều này có nghĩa:

Mỗi Business chỉ được phép truy cập dữ liệu của chính mình.

---

## Quy tắc 1

Mọi câu truy vấn của Owner đều phải lọc theo:

```
business_id
```

Ví dụ:

```
SELECT *

FROM appointments

WHERE business_id = ?
```

---

## Quy tắc 2

Owner chỉ được quản lý Business mà mình sở hữu.

Ví dụ:

```
business.owner_id

=

user.id
```

Nếu Owner A cố truy cập Business B

↓

Hệ thống phải từ chối.

---

## Quy tắc 3

Staff chỉ được xem các Appointment được phân công.

Ví dụ:

```
appointment.staff_id

=

staff_profile.id
```

Staff không được xem lịch của Staff khác.

---

## Quy tắc 4

API Public Booking chỉ hiển thị:

- Business đang hoạt động.
- Service đang hoạt động.
- Staff đang hoạt động.
- Slot còn trống.

Không được trả về:

- lịch hẹn nội bộ.
- khách hàng.
- thông tin nhân viên không cần thiết.

---

# 18. Ghi chú triển khai (Implementation Notes)

Đây là một số quy ước kỹ thuật được sử dụng trong MVP.

---

## ID

Tất cả bảng đều sử dụng:

```
BIGINT

AUTO_INCREMENT
```

để đơn giản trong quá trình phát triển.

---

## Thời gian

Audit Time:

```
created_at

updated_at
```

được lưu dưới dạng:

```
DATETIME
```

---

## Thời gian Appointment

Lịch hẹn được lưu thành:

```
appointment_date

DATE
```

```
start_time

TIME
```

```
end_time

TIME
```

Thay vì:

```
DATETIME
```

Lý do:

- Dễ tìm lịch theo ngày.
- Dễ sinh Slot.
- Dễ kiểm tra trùng lịch.
- MVP chỉ sử dụng một múi giờ.

---

## Timezone

Business vẫn có trường:

```
timezone
```

mặc dù MVP mặc định dùng:

```
Asia/Ho_Chi_Minh
```

Việc giữ trường này sẽ giúp dễ mở rộng khi:

- hỗ trợ nhiều quốc gia.
- nhiều múi giờ.
- nhiều chi nhánh.

---

## Soft Delete

Đối với:

- Service
- Staff

không nên xóa cứng.

Thay vào đó:

```
active = false
```

Điều này giúp:

- giữ nguyên lịch sử Appointment.
- không làm mất khóa ngoại.
- dễ khôi phục dữ liệu.

---

# Tổng kết

Đến đây, tài liệu **ERD của ClientFlow MVP** đã hoàn chỉnh.

Hệ thống gồm **14 bảng**, được chia thành các nhóm chức năng:

### 1. Quản lý người dùng

- roles
- users

### 2. Quản lý Business

- businesses

### 3. Quản lý dịch vụ

- services

### 4. Quản lý nhân viên

- staff_profiles
- staff_services
- working_hours
- staff_time_off

### 5. Quản lý lịch nghỉ

- business_exceptions

### 6. Quản lý khách hàng

- customers

### 7. Quản lý lịch hẹn

- appointments
- appointment_notes

### 8. Hệ thống

- notifications
- password_reset_tokens

Toàn bộ dữ liệu được phân tách theo **business_id**, đảm bảo mỗi Business chỉ có thể truy cập và quản lý dữ liệu của chính mình.