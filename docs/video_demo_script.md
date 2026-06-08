# Kịch Bản Video Demo Dự Án MinLish Lite

- **Thời lượng dự kiến:** 3.5 - 4 phút
- **Số lượng người trình bày:** 1 người
- **Chuẩn bị trước khi quay:**
  - Cài đặt sẵn ứng dụng trên máy ảo (Emulator) hoặc thiết bị thật, chia sẻ màn hình rõ nét.
  - Xóa dữ liệu cũ để demo từ màn hình Onboarding, hoặc chuẩn bị sẵn 1 bộ dữ liệu mẫu (có 1-2 Deck, lịch sử học vài ngày để có biểu đồ Progress).

---

## 🕒 Phân Bổ Thời Gian (Timeline)

| Thời gian | Nội dung chính | Hành động trên màn hình (Screencast) |
| :--- | :--- | :--- |
| `0:00 - 0:30` | Giới thiệu bản thân, dự án, bài toán & giải pháp. | Slide giới thiệu hoặc màn hình Splash Screen của app. |
| `0:30 - 1:15` | Onboarding, Home Screen & Quản lý Deck (CSV). | Nhập thông tin Onboarding, xem Dashboard, tạo/import bộ từ. |
| `1:15 - 2:15` | Quản lý từ vựng, Tra từ điển & Chế độ học (SRS). | Thêm từ mới (auto-fetch API), vào Study Mode lật thẻ, chọn mức độ khó. |
| `2:15 - 2:50` | Tiến độ học tập (Progress) & Cài đặt (Settings). | Xem biểu đồ Streak, tỉ lệ nhớ, sang phần Setting đổi mục tiêu/profile. |
| `2:50 - 3:20` | Điểm nhấn kỹ thuật (Tech Stack & Architecture). | Hiện sơ đồ kiến trúc MVVM hoặc lướt qua code (ngắn gọn). |
| `3:20 - 3:40` | Tổng kết và Cảm ơn. | Màn hình Home app / Slide Thank You. |

---

## 🎬 Chi Tiết Lời Thoại & Hành Động

### Phần 1: Giới thiệu (0:00 - 0:30)
- **Lời thoại:** Xin chào thầy cô và các bạn, em là [Tên của bạn]. Hôm nay, em xin phép demo dự án cuối kỳ mang tên **MinLish Lite**. Dự án này sinh ra để giải quyết vấn đề "đường cong lãng quên" khi học ngoại ngữ. Ứng dụng giúp người học quản lý từ vựng cá nhân và tối ưu hóa việc ghi nhớ bằng thuật toán Lặp lại ngắt quãng (Spaced Repetition System - SRS). Đặc biệt, mọi dữ liệu đều được xử lý "local-first" để đảm bảo trải nghiệm mượt mà, không độ trễ.

### Phần 2: Onboarding, Home & Quản lý bộ từ (0:30 - 1:15)
- **Lời thoại:** *(Thao tác trên màn hình: Mở app, điền Onboarding).* Khi mới mở app, người dùng sẽ qua màn hình Onboarding để nhập tên, chọn trình độ và thiết lập mục tiêu số từ mới mỗi ngày.
- **Lời thoại:** *(Thao tác: Vào Home Screen).* Đây là màn hình chính. Màn hình này cung cấp một Banner trực quan để nhắc nhở mục tiêu học tập hàng ngày, giúp duy trì thói quen học.
- **Lời thoại:** *(Thao tác: Chuyển sang tab Deck, bấm tạo Deck hoặc Import CSV).* Chuyển sang tab quản lý Bộ từ (Deck). Tại đây, người dùng có thể tự tạo bộ từ mới hoặc Import hàng loạt từ vựng qua file CSV rất tiện lợi. Tính năng này giúp tiết kiệm cực kỳ nhiều thời gian nhập liệu thủ công.

### Phần 3: Thêm từ vựng & Chế độ học SRS (1:15 - 2:15)
- **Lời thoại:** Bây giờ, em sẽ demo chi tiết cách thêm và học từ. Để thêm một từ mới, chỉ cần nhập từ tiếng Anh. *(Thao tác: Nhập từ tiếng Anh, ví dụ "Ephemeral" rồi bấm nút tải).* 
- **Lời thoại:** Điểm đặc biệt của MinLish Lite là tích hợp API Từ điển và Dịch thuật từ xa. App sẽ tự động lấy định nghĩa và bản dịch tiếng Việt về ngay lập tức.
- **Lời thoại:** *(Thao tác: Quay lại, bấm "Review Today" hoặc vào Study Mode).* Tiếp theo là tính năng cốt lõi nhất: Chế độ Học (Study Mode). 
- **Lời thoại:** Khi lật một thẻ Flashcard, người học tự đánh giá khả năng nhớ của mình theo 4 mức độ: Again, Hard, Good, Easy. Dựa vào lựa chọn này, thuật toán **SRS** được tích hợp sẵn sẽ tính toán chính xác thời điểm tương lai để lặp lại từ này, giúp đưa từ vựng vào trí nhớ dài hạn một cách hiệu quả nhất.

### Phần 4: Tiến độ (Progress) & Cài đặt (2:15 - 2:50)
- **Lời thoại:** *(Thao tác: Chuyển sang tab Progress).* Để người dùng có thêm động lực, ứng dụng cung cấp hệ thống Tracking chi tiết. Tại đây hiển thị biểu đồ học tập theo tuần, chuỗi ngày học liên tục (Streak), và tỷ lệ nhớ từ (Retention rate). 
- **Lời thoại:** *(Thao tác: Chuyển sang tab Settings).* Ở màn hình Cài đặt, người dùng có thể thay đổi linh hoạt mục tiêu học tập, chỉnh sửa hồ sơ cá nhân và quản lý việc bật/tắt thông báo nhắc nhở học tập hàng ngày.

### Phần 5: Dấu ấn kỹ thuật (2:50 - 3:20)
- **Lời thoại:** Về khía cạnh kỹ thuật, dự án được phát triển hoàn toàn bằng ngôn ngữ **Kotlin** với bộ công cụ UI hiện đại **Jetpack Compose**.
- **Lời thoại:** *(Có thể show nhanh 1 slide sơ đồ hoặc nói chay).* Hệ thống tuân thủ nghiêm ngặt chuẩn kiến trúc **MVVM** kết hợp Repository Pattern. Dữ liệu được lưu trữ an toàn tại thiết bị thông qua **Room Database**, và mọi luồng xử lý bất đồng bộ đều được quản lý mượt mà nhờ **Kotlin Coroutines & Flow**.

### Phần 6: Lời kết (3:20 - 3:40)
- **Lời thoại:** Dù vẫn còn tiềm năng mở rộng thêm các tính năng như Cloud Sync, nhưng phiên bản MinLish Lite hiện tại đã là một sản phẩm hoàn thiện, hoạt động mượt mà và áp dụng đúng phương pháp khoa học vào việc học từ vựng.
- **Lời thoại:** Cảm ơn thầy cô và các bạn đã dành thời gian theo dõi phần demo của em!

---

## 💡 Mẹo để video chuyên nghiệp hơn:
1. **Thu âm trước, quay hình sau:** Để khớp thời gian hoàn hảo và không bị vấp, hãy đọc kịch bản và thu âm trôi chảy trước. Sau đó dùng phần mềm quay màn hình thao tác chuột khớp với lời nói.
2. **Hiệu ứng thu phóng (Zoom):** Khi đề cập đến tính năng gọi API tra từ điển, hoặc giải thích biểu đồ Streak, hãy edit video zoom to phần đó lên để người xem tập trung.
3. **Thêm phụ đề (Subtitle):** Phụ đề sẽ giúp video trông chuyên nghiệp hơn và người xem dễ theo dõi thông tin, đặc biệt là các thuật ngữ kỹ thuật.
