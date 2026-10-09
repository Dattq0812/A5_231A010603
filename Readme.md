# Lab A5 - Chuyển màn hình & Truyền dữ liệu (Báo cáo Nâng cao)

Dự án này là phiên bản mở rộng của Lab A5, tập trung giải quyết hai bài tập nâng cao **NC1** và **NC2**. Hệ thống trình diễn cách luân chuyển dữ liệu phức tạp (`ArrayList` và `Parcelable`) qua lại giữa nhiều màn hình (Activities) mà không làm mất trạng thái dữ liệu, sử dụng `ActivityResult API` hiện đại của Android.

## 🌟 Tóm tắt tính năng Nâng cao

### 1. NC1: Chuyền dữ liệu qua 3 màn hình (Main ↔ Detail ↔ Note)
Thay vì luồng giao tiếp 2 chiều đơn giản, ứng dụng đã được thiết kế lại để dữ liệu có thể "đi du lịch" qua 3 trạm:
* **Màn hình 1 (MainActivity):** Nơi bắt đầu khởi tạo đối tượng `Contact` (Họ tên, SĐT, Email, Ghi chú trống).
* **Màn hình 2 (DetailActivity):** Nhận thông tin, hiển thị chi tiết. Tại đây có nút **Thêm ghi chú** để gọi Màn hình 3.
* **Màn hình 3 (NoteActivity):** Một màn hình phụ cho phép nhập nội dung văn bản.
* **Luồng trả về:** Màn hình 3 gói text vào Intent trả về Màn hình 2 -> Màn hình 2 nhúng text này vào thuộc tính `ghiChu` của đối tượng `Contact` -> Cuối cùng Màn hình 2 trả toàn bộ đối tượng đã được cập nhật (đổi tên + thêm ghi chú) về lại Màn hình 1 để hiển thị.

### 2. NC2: Truyền một danh sách đối tượng (ArrayList)
Để chứng minh khả năng truyền tải dữ liệu lớn qua Intent, hệ thống không chỉ gửi đi 1 đối tượng `Contact` đơn lẻ mà gửi đi toàn bộ một `ArrayList<Contact>`. Tính năng này được tích hợp liền mạch với NC1.

## 🚀 Chi tiết Luồng hoạt động & Xử lý Kỹ thuật (Tích hợp NC1 + NC2)

Sự kết hợp giữa NC1 và NC2 tạo ra một luồng xử lý chặt chẽ như sau:

1. **Cấu trúc lại đối tượng (Parcelable):**
    * Lớp `Contact` được bổ sung thêm trường `ghiChu` kiểu String.
    * Cập nhật các hàm `writeToParcel` và constructor đọc dữ liệu từ `Parcel` để đảm bảo chuỗi byte được nén và giải nén đúng thứ tự.

2. **Gửi danh sách (Main → Detail):**
    * Trong `MainActivity`, khởi tạo một `ArrayList<Contact>` và thêm phần tử đầu tiên (index 0) bằng dữ liệu người dùng nhập, kèm theo vài phần tử giả lập (mock data).
    * Đóng gói mảng này bằng `intent.putParcelableArrayListExtra()`.

3. **Xử lý trung gian & Ghi chú (Detail ↔ Note):**
    * `DetailActivity` sử dụng `IntentCompat.getParcelableArrayListExtra()` để khôi phục toàn bộ danh sách.
    * Lấy phần tử `danhSachContact.get(0)` ra làm đối tượng thao tác chính.
    * Khi gọi `NoteActivity` và nhận về chuỗi ghi chú, chuỗi này được `setGhiChu()` trực tiếp vào phần tử index 0. Do tính chất tham chiếu của Java, phần tử bên trong danh sách cũng được cập nhật tự động.

4. **Hoàn trả danh sách (Detail → Main):**
    * Khi người dùng bấm "Lưu & quay lại", `DetailActivity` cập nhật lại Họ tên mới và đóng gói lại **toàn bộ** `ArrayList` để trả về cho `MainActivity`.
    * `MainActivity` nhận mảng trả về, bóc tách phần tử index 0 để in ra giao diện, xác nhận rằng cả thao tác đổi tên (từ Màn hình 2) và thao tác thêm ghi chú (từ Màn hình 3) đều được lưu giữ nguyên vẹn trên cùng một đối tượng trong mảng.

## 🛠 Bài học cốt lõi
* Sử dụng thành thạo `ActivityResultLauncher` và `ActivityResultContracts.StartActivityForResult()` thay thế cho `onActivityResult` (đã lỗi thời).
* Nắm vững kỹ thuật truyền tải mảng đối tượng `ArrayList` thông qua lớp `Parcelable` giúp tối ưu hóa hiệu suất bộ nhớ.
* Hiểu rõ tính chất tham chiếu đối tượng trong Java khi thao tác với các phần tử bên trong một List và luân chuyển qua Intent.