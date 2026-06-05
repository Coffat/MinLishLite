# Phân Chia Dự Án MinLishLite Theo Tính Năng & Màn Hình (Chi Tiết Toàn Bộ Codebase)

Dự án **MinLishLite** được xây dựng theo kiến trúc MVVM với Jetpack Compose. Để đảm bảo **cả 2 thành viên đều nắm được từ A-Z (từ UI, ViewModel xuống tới Database/API)** và phân chia khối lượng code cân bằng nhất, dự án được chia theo các cụm tính năng cụ thể dưới đây. *Lưu ý: Mọi class, file trong dự án đều đã được chỉ định vào các phần tương ứng.*

---

## 📚 Thành viên 1: Quản lý Nội dung (Decks, Words), Thiết lập (Settings) & Tích hợp API
Thành viên này chịu trách nhiệm luồng tạo, chỉnh sửa và quản lý từ vựng/bộ từ vựng, màn hình chính, thiết lập của user, cũng như cách ứng dụng giao tiếp với API bên ngoài (Từ điển, Dịch thuật).

### 1. Presentation Layer (UI & ViewModels)
- **Quản lý Bộ từ (Decks)**: `DeckListScreen`, `DeckDetailScreen`, `AddEditDeckScreen`, `DeckSummarySection`, cùng với các `DeckListViewModel`, `DeckDetailViewModel`, `AddEditDeckViewModel`.
- **Quản lý Từ vựng (Words)**: `WordDetailScreen`, `AddEditWordScreen`, cùng với `WordDetailViewModel`, `AddEditWordViewModel`.
- **Màn hình chính & Khởi tạo**: `HomeScreen`, `HomeViewModel` và `OnboardingScreen`, `OnboardingViewModel`.
- **Cài đặt**: `SettingsScreen`, `SettingsViewModel`.

### 2. Data Layer (Room Database, API, Repository)
- **Database Local (Room)**: Các Entity `WordEntity`, `DeckEntity` và DAOs `WordDao`, `DeckDao`.
- **Dữ liệu Preference**: `SettingsPreferences`.
- **API (Retrofit)**: 
  - Gọi API qua `DictionaryApiService` và `TranslationApiService`.
  - Các DTOs: `DictionaryEntryDto`, `TranslationResponseDto`.
- **Repositories**: `WordRepository`, `DeckRepository`, `DictionaryRepository`, `TranslationRepository`, `SettingsRepository`.
- **Data Models**: `DictionaryResult`.

### 3. Core/Utils
- `WordValidator` (Validate dữ liệu từ).
- `CsvHelper` (Hỗ trợ import/export file).
- `DictionaryException` (Xử lý lỗi từ điển).

---

## 🧠 Thành viên 2: Học tập (Study), Ôn tập (Review), Theo dõi Tiến độ (Progress)
Thành viên này chịu trách nhiệm luồng học từ vựng qua Flashcards, logic tính toán thời gian ôn tập (Spaced Repetition), theo dõi thống kê và hệ thống phát âm.

### 1. Presentation Layer (UI & ViewModels)
- **Học & Ôn tập (Study/Review)**: `StudyScreen`, `StudyViewModel`, `ReviewTodayScreen`, `ReviewTodayViewModel`.
- **Component dùng riêng cho Study**: `Flashcard`, `ReviewRatingButtons`, `StudyMode`, `StudyReminderBanner`.
- **Thống kê (Progress)**: `ProgressScreen`, `ProgressViewModel`.

### 2. Data Layer (Room Database, Repository)
- **Database Local (Room)**: Các Entity `ReviewHistoryEntity`, `UserEntity` và DAOs `ReviewHistoryDao`, `UserDao`.
- **Repositories**: `StudyRepository`, `ProgressRepository`, `UserRepository`.
- **Data Models**: `ReviewResult`, `ProgressAnalytics`.

### 3. Core/Utils (Thuật toán & Âm thanh)
- **Thuật toán Spaced Repetition**: `SrsCalculator` (Thuật toán tính điểm/ngày ôn).
- **Thống kê**: `ProgressCalculator`.
- **Xử lý Âm thanh**: `PronunciationAudioPlayer`, `PronunciationHelper`.

---

## 🤝 Phần chung: Nền tảng ứng dụng (Cả 2 thành viên đều phải hiểu)
Đây là các file mang tính cấu trúc hệ thống, dùng chung cho toàn bộ app. Cả 2 thành viên đều cần đọc để hiểu app chạy lên như thế nào.

### 1. Kiến trúc, Khởi tạo & Cấu hình App
- `MainActivity.kt`: Điểm entry point của Android App.
- `MinLishApplication.kt`: Application class, thường dùng để khởi tạo biến toàn cục.
- `di/AppContainer.kt`: Dependency Injection tự làm (hoặc manual DI), cung cấp Repository và DAO cho các ViewModel.
- `data/local/database/AppDatabase.kt`: Class tổng khai báo Room Database.

### 2. Điều hướng (Navigation)
Toàn bộ thư mục `presentation/navigation`:
- `AppNavigation.kt`, `NavGraph.kt`, `Routes.kt`, `NavigationDestination.kt`
- `BottomNavigationBar.kt` (Thanh điều hướng dưới đáy màn hình).

### 3. UI Components dùng chung (Core UI)
- **Theme**: Toàn bộ `ui/theme` (`Theme.kt`, `Color.kt`, `Type.kt`).
- **Shared Components**: Các file trong `presentation/component` như `AppButton`, `AppTextField`, `LoadingState`, `ErrorState`, `SectionHeader`, `EmptyState`.

### 4. Tiện ích chung
- `AppLogger.kt`: Dùng để log lỗi và trace luồng code.

---
### 💡 Câu hỏi vấn đáp thường gặp để kiểm tra độ hiểu sâu (Cross-check):
1. **Dành cho cả 2:** Giải thích luồng Data Flow của app? (Từ khi bấm UI trên Compose -> ViewModel -> Repository -> DAO/API -> UI cập nhật).
2. **Hỏi Thành viên 1:** Nếu tôi muốn thêm một field mới cho bảng `DeckEntity`, tôi cần phải sửa những file nào từ Data lên tới UI?
3. **Hỏi Thành viên 2:** Thuật toán `SrsCalculator` hoạt động ra sao, dựa vào các tham số nào để trả về `ReviewResult` (ngày ôn tập kế tiếp)? Giải thích State của `StudyScreen` (lật thẻ) được kiểm soát như thế nào?
