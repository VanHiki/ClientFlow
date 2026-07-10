# Quy tắc Đặt lịch (Booking Rules) - ClientFlow

> Phiên bản: MVP
> Múi giờ: Asia/Ho_Chi_Minh

---

# 1. Các quy định mặc định

| Nội dung | Giá trị |
|----------|----------|
| Múi giờ | Asia/Ho_Chi_Minh |
| Khoảng cách giữa các slot | 30 phút |
| Lưu thời gian lịch | appointment_date, start_time, end_time |
| Trạng thái mặc định | PENDING |
| Đặt lịch công khai | Không cần đăng nhập |
| Mã Booking | Bắt buộc và duy nhất |
| Khách hàng chưa tồn tại | Tự tạo trong Business tương ứng |
| Kiểm tra trùng lịch | Cùng nhân viên, cùng ngày và thời gian bị chồng lấn |

---

# 2. Trạng thái lịch hẹn

| Trạng thái | Ý nghĩa | Có giữ slot? |
|------------|----------|--------------|
| PENDING | Khách vừa tạo lịch, đang chờ xác nhận | Có |
| CONFIRMED | Lịch đã được xác nhận | Có |
| CHECKED_IN | Khách đã đến | Có |
| COMPLETED | Đã hoàn thành dịch vụ | Không |
| CANCELLED | Khách hủy lịch | Không |
| NO_SHOW | Khách không đến | Không |

---

# 3. Luồng chuyển trạng thái

```text
PENDING
    ↓
CONFIRMED
    ↓
CHECKED_IN
    ↓
COMPLETED
```

Ngoài ra còn cho phép:

```text
PENDING → CANCELLED

CONFIRMED → CANCELLED

CONFIRMED → NO_SHOW

CHECKED_IN → CANCELLED
```

Sau khi đã ở trạng thái:

- COMPLETED
- CANCELLED
- NO_SHOW

thì không được thay đổi nữa.

---

# 4. Quy tắc sinh Slot

Đầu vào gồm:

- businessSlug
- serviceId
- appointmentDate

Backend sẽ thực hiện các bước sau:

1. Tìm Business theo slug.
2. Nếu Business không tồn tại hoặc đã bị khóa thì dừng.
3. Tìm Service theo serviceId và businessId.
4. Nếu Service không tồn tại hoặc đã bị khóa thì dừng.
5. Lấy danh sách nhân viên đang hoạt động và có thể thực hiện dịch vụ.
6. Lấy lịch làm việc của từng nhân viên theo đúng thứ trong tuần.
7. Nếu Business nghỉ vào ngày đó thì không sinh slot.
8. Loại bỏ khoảng thời gian nhân viên xin nghỉ.
9. Sinh các slot cách nhau 30 phút.
10. Đảm bảo thời lượng dịch vụ không vượt quá giờ kết thúc ca làm.
11. Nếu là ngày hiện tại thì loại bỏ các slot đã qua.
12. Loại bỏ các slot bị trùng với lịch đã đặt.
13. Trả về danh sách slot còn trống.

---

# 5. Quy tắc kiểm tra trùng lịch

Hai lịch được xem là trùng khi:

- Cùng nhân viên.
- Cùng ngày.
- Lịch cũ có trạng thái:
    - PENDING
    - CONFIRMED
    - CHECKED_IN
- Khoảng thời gian giao nhau.

Điều kiện:

```text
new_start < existing_end

new_end > existing_start
```

Ví dụ:

```
09:00 - 10:00

✓ Trùng
09:30 - 10:30

✓ Trùng
08:30 - 09:30

✗ Không trùng
10:00 - 11:00

✗ Không trùng
08:00 - 09:00
```

Khi tạo lịch mới:

- Luôn kiểm tra lại trong Transaction.
- Không tin hoàn toàn dữ liệu từ API Available Slots.
- Nếu hai người đặt cùng một slot thì chỉ một người được thành công.

---

# 6. Kiểm tra dữ liệu khi khách đặt lịch

Từ chối yêu cầu nếu:

- Ngày đặt đã qua.
- Giờ đặt đã qua trong ngày hiện tại.
- Business bị khóa.
- Service bị khóa.
- Staff bị khóa.
- Staff không thực hiện dịch vụ.
- Slot ngoài giờ làm.
- Slot bị trùng.
- Slot trùng thời gian nghỉ của nhân viên.
- Business nghỉ ngày đó.
- Tên khách hàng để trống.
- Số điện thoại không hợp lệ.
- Email sai định dạng (nếu có).

Quy tắc riêng với khách hàng:

- Nếu owner tạo lịch cho Customer đã bị khóa thì từ chối.
- Nếu public booking tìm thấy Customer đã bị khóa qua số điện thoại, hệ thống kích hoạt lại Customer đó trước khi tạo lịch.
- Mỗi lịch mới có một booking code công khai và duy nhất.
- Khách chỉ được tự hủy lịch PENDING hoặc CONFIRMED trước giờ bắt đầu ít nhất 2 giờ.

---

# 7. Enum Role

```text
OWNER
STAFF
CUSTOMER
ADMIN
```

Ý nghĩa:

- OWNER: Quản lý toàn bộ dữ liệu của Business.
- STAFF: Quản lý các lịch được phân công.
- CUSTOMER: Không bắt buộc trong MVP.
- ADMIN: Quản trị toàn hệ thống.

---

# 8. Loại ngày nghỉ

```text
CLOSED_DAY
HOLIDAY
SPECIAL_CLOSURE
```

- CLOSED_DAY: Nghỉ định kỳ.
- HOLIDAY: Nghỉ lễ.
- SPECIAL_CLOSURE: Nghỉ đột xuất (bảo trì, sự kiện...).

---

# 9. Loại thông báo

```text
APPOINTMENT_CREATED
APPOINTMENT_CONFIRMED
APPOINTMENT_CANCELLED
APPOINTMENT_COMPLETED
APPOINTMENT_REMINDER
PASSWORD_RESET_REQUESTED
```

Trong MVP:

- Chỉ lưu thông báo trong Database.
- Chưa gửi Email hoặc SMS.

---

# 10. Quy tắc về thời gian

- Tất cả phép tính đều dùng múi giờ Asia/Ho_Chi_Minh.
- Database lưu thời gian theo giờ địa phương.
- Không dùng timezone mặc định của server.

---

# 11. Quy tắc xóa dữ liệu

- Không xóa cứng Service nếu đã có lịch hẹn.
- Nên dùng `active = false` để ẩn Service, Staff hoặc Customer.
- Lịch CANCELLED và NO_SHOW vẫn được lưu.
- Chỉ lịch COMPLETED được tính doanh thu.
- Lịch CANCELLED không tính doanh thu.

---

# 12. Quy tắc Dashboard

Dashboard chỉ thống kê trong Business hiện tại.

Bao gồm:

- Số lịch trong ngày.
- Doanh thu từ các lịch COMPLETED.
- Số lượng lịch:
    - PENDING
    - CANCELLED
    - COMPLETED

Tất cả dữ liệu đều được lọc theo `business_id`.
