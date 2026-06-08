# Bảng Tự Chấm Điểm Đồ Án - MinLishLite

Dựa theo Rubric đánh giá đồ án, nhóm tự đánh giá chất lượng và tiến độ hoàn thành dự án **MinLishLite** (Ứng dụng học từ vựng Flashcard kết hợp thuật toán lặp lại ngắt quãng - Spaced Repetition System) như sau:

---

## 1. Bài nộp (Điểm tối đa: 5)
- **Tiêu chí:** Slide, video demo, source code.
- **Hiện trạng:** Nhóm đã chuẩn bị đầy đủ Slide thuyết trình, Video demo (quay lại toàn bộ luồng sử dụng app) và Source code đẩy đủ lên Github.
- **Điểm tự chấm:** **5 / 5**

---

## 2. Chất lượng sản phẩm (Điểm tối đa: 30)
- **Tính đúng đắn của chức năng:** Toàn bộ chức năng từ quản lý (thêm/sửa/xóa), học từ, ôn tập, đến theo dõi tiến độ đều hoạt động chính xác.
- **UI/UX & Trải nghiệm:** Giao diện được code hoàn toàn bằng **Jetpack Compose** theo phong cách Modern UI. Hỗ trợ mượt mà các thao tác vuốt, lật thẻ (Flashcard flip animation), thiết kế đồng nhất về màu sắc và typography.
- **Kiến trúc phần mềm:** Sử dụng chuẩn **MVVM** (Model-View-ViewModel) kết hợp Clean Architecture. Tách biệt rõ ràng Presentation Layer, Data Layer (Repository/DAO) và Core Utilities.
- **Khả năng bảo trì & mở rộng:** Code chia theo tính năng (feature-based) rất rõ ràng (xem file `phan_chia_du_an.md`). Dễ dàng thêm tính năng mới mà không ảnh hưởng tới luồng cũ.
- **Unit test, Performance, Security:** 
  - Đã viết Unit Test cho các logic quan trọng: `SrsCalculatorTest`, `WordValidatorTest`, `ProgressCalculatorTest` cùng các Repository Test.
  - Sử dụng Room Database tối ưu performance qua Coroutines/Flow.
- **Điểm tự chấm:** **30 / 30**

---

## 3. Hoàn thành chức năng cơ bản (Điểm tối đa: 30)
- **Tạo bộ từ vựng:** Đã hoàn thành (Màn hình `DeckListScreen`, `AddEditDeckScreen`).
- **Thêm từ vựng:** Đã hoàn thành (Màn hình `WordDetailScreen`, `AddEditWordScreen`), validate từ vựng chặt chẽ.
- **Học/ôn từ vựng:** Đã hoàn thành hệ thống Flashcard (`StudyScreen`, `StudyMode`) cho phép người dùng tự chấm điểm mức độ nhớ.
- **Điểm tự chấm:** **30 / 30**

---

## 4. Hoàn thành chức năng nâng cao (Điểm tối đa: 25)
- **Đăng ký, đăng nhập, hồ sơ:** Đã xử lý xác thực và quản lý hồ sơ người dùng (`UserEntity`, cấu hình mục tiêu học tập, cấp độ).
- **Import, export:** Xây dựng `CsvHelper` hỗ trợ nạp/xuất dữ liệu bộ từ vựng nhanh chóng qua file `.csv`.
- **Ôn tập thông minh (SRS):** Cài đặt thuật toán lặp lại ngắt quãng (`SrsCalculator`) tính toán chính xác ngày cần ôn tập lại của từng từ dựa trên trí nhớ của người dùng.
- **Quản lý tiến độ học tập:** Màn hình `ProgressScreen` theo dõi số từ đã học, điểm số, và thống kê (`ProgressAnalytics`).
- **Hệ thống nhắc nhở:** Đã cấu hình và xin quyền cấp thông báo (`POST_NOTIFICATIONS` ở `SettingsScreen`), kết hợp Banner nhắc nhở trên HomeScreen (`StudyReminderBanner`) để giục người dùng học bài.
- **Điểm tự chấm:** **25 / 25**

---

## 5. Sáng tạo & Mở rộng (Điểm tối đa: 10)
- **Tích hợp API Từ điển thực tế:** Thay vì chỉ nhập thủ công, app tích hợp trực tiếp **Retrofit** gọi API từ điển (`DictionaryApiService`) và API Dịch thuật để tự động điền nghĩa, phiên âm và ví dụ cho từ.
- **Hệ thống phát âm (Text-to-Speech):** Tích hợp xử lý phát âm chuẩn bằng thư viện Android TTS (`PronunciationAudioPlayer`, `PronunciationHelper`), giúp người dùng nghe trực tiếp phát âm trên Flashcard.
- **Điểm tự chấm:** **10 / 10**

---

## TỔNG KẾT
**TỔNG ĐIỂM TỰ CHẤM: 100 / 100**

*Sản phẩm được đầu tư rất chỉn chu từ kiến trúc, UI/UX, đến các thuật toán cốt lõi, hoàn toàn đáp ứng và vượt kỳ vọng so với yêu cầu của đồ án.*
