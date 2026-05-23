# PLAN DỰ ÁN ANDROID: MinLish Lite – Ứng dụng học từ vựng tiếng Anh

## 0. Mục tiêu của plan

Plan này dùng để triển khai đồ án môn **Lập trình Android cơ bản** cho ứng dụng **MinLish Lite**.

Mục tiêu là xây dựng một app vừa sức để làm một mình, nhưng vẫn đáp ứng đầy đủ các nhóm yêu cầu chính trong file dự án:

- User Management
- Vocabulary Management
- Learning Engine / Flashcard / SRS
- Practice Module
- Analytics & Progress
- Notification System
- Yêu cầu phi chức năng: usability, performance, security ở mức phù hợp với app Android cơ bản

Đồng thời app phải thể hiện đầy đủ các topic Android trong file topic:

- Compose Fundamentals
- State & Recomposition
- Navigation
- MVVM
- ViewModel & State Management
- Networking
- Coroutines & Async Programming
- Local Storage
- Repository Pattern
- Dependency Injection
- Testing & Debugging
- Performance & Optimization

---

# 1. Chốt phạm vi dự án

## 1.1. Tên đề tài

**MinLish Lite – Ứng dụng hỗ trợ học từ vựng tiếng Anh bằng Flashcard và Spaced Repetition**

## 1.2. Mô tả ngắn

MinLish Lite là ứng dụng Android giúp người dùng tạo bộ từ vựng, thêm từ mới, học bằng flashcard, đánh giá mức độ ghi nhớ bằng Again / Hard / Good / Easy, tự động tính lịch ôn tiếp theo theo SRS đơn giản, xem tiến độ học và nhận nhắc nhở học hằng ngày.

## 1.3. Phạm vi nên làm

App sẽ làm theo hướng **offline-first**, tức là dữ liệu chính được lưu cục bộ bằng Room Database. Networking chỉ dùng để tra nghĩa từ qua Dictionary API, không làm backend phức tạp.

## 1.4. Chức năng có trong bản nộp

- Đăng nhập giả lập / tiếp tục dưới dạng khách
- Hồ sơ người dùng cục bộ
- Quản lý bộ từ vựng
- Quản lý từ vựng
- Tra từ qua Dictionary API
- Học flashcard
- SRS cơ bản
- Danh sách từ cần ôn hôm nay
- Dashboard tiến độ
- Cài đặt mục tiêu học
- Nhắc học hằng ngày bằng local notification hoặc hiển thị nhắc học trong app
- Unit test cho SRS logic và ViewModel quan trọng

## 1.5. Chức năng không làm trong bản cơ bản

Các phần sau được đưa vào mục “hướng phát triển”:

- Google Login thật
- Backend riêng
- JWT thật
- Bcrypt password thật
- Đồng bộ cloud
- Import / Export Excel
- Email notification
- SM-2 đầy đủ 100%

Lý do: các phần này quá rộng so với môn Android cơ bản và làm lệch trọng tâm Android.

---

# 2. Công nghệ sử dụng

## 2.1. Ngôn ngữ và UI

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose

## 2.2. Architecture

- MVVM
- Repository Pattern
- StateFlow
- Unidirectional Data Flow

## 2.3. Data

- Room Database
- DataStore Preferences
- Retrofit
- Kotlinx Serialization hoặc Gson/Moshi

## 2.4. Async

- Kotlin Coroutines
- Flow
- viewModelScope

## 2.5. Dependency Injection

Có 2 lựa chọn:

### Lựa chọn dễ hơn

Manual Dependency Injection bằng `AppContainer`.

### Lựa chọn chuyên nghiệp hơn

Hilt.

Khuyến nghị nếu bạn đang học Android cơ bản: dùng **Manual DI** trước để dễ hiểu. Nếu còn thời gian thì refactor sang Hilt.

## 2.6. Testing

- JUnit
- Coroutine Test
- Fake Repository

---

# 3. Kiến trúc tổng thể

## 3.1. Mô hình kiến trúc

App dùng mô hình:

```text
UI Layer
↓
ViewModel Layer
↓
Repository Layer
↓
Local Data Source / Remote Data Source
```

## 3.2. Data flow chuẩn

```text
User Action
→ Composable gửi Event
→ ViewModel xử lý
→ Repository lấy/lưu dữ liệu
→ ViewModel cập nhật UiState
→ Compose tự recomposition
```

## 3.3. Cấu trúc thư mục đề xuất

```text
com.example.minlishlite
│
├── MainActivity.kt
│
├── core
│   ├── ui
│   ├── util
│   ├── result
│   └── notification
│
├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   ├── database
│   │   └── preference
│   │
│   ├── remote
│   │   ├── api
│   │   └── dto
│   │
│   ├── mapper
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── di
│
├── ui
│   └── theme
│
└── presentation
    ├── navigation
    ├── component
    ├── onboarding
    ├── home
    ├── deck
    ├── word
    ├── study
    ├── progress
    └── settings
```

---

# 4. Database design

## 4.1. UserEntity

Dùng để đáp ứng phần User Management ở mức local.

```kotlin
data class UserEntity(
    val id: Int,
    val name: String,
    val email: String?,
    val goal: String,
    val level: String,
    val createdAt: Long
)
```

## 4.2. DeckEntity

Dùng cho chức năng tạo bộ từ vựng.

```kotlin
data class DeckEntity(
    val id: Int,
    val name: String,
    val description: String,
    val tag: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

## 4.3. WordEntity

Dùng cho chức năng thêm từ vựng.

```kotlin
data class WordEntity(
    val id: Int,
    val deckId: Int,
    val word: String,
    val pronunciation: String,
    val meaning: String,
    val description: String,
    val example: String,
    val collocation: String,
    val relatedWords: String,
    val note: String,
    val level: String,
    val nextReviewAt: Long,
    val lastReviewedAt: Long?,
    val reviewCount: Int,
    val correctCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)
```

## 4.4. ReviewHistoryEntity

Dùng để lưu lịch sử học và tính thống kê.

```kotlin
data class ReviewHistoryEntity(
    val id: Int,
    val wordId: Int,
    val deckId: Int,
    val result: String,
    val reviewedAt: Long
)
```

## 4.5. SettingsEntity hoặc DataStore

Dùng để lưu cấu hình cá nhân.

```kotlin
data class UserSettings(
    val newWordsPerDay: Int,
    val reminderEnabled: Boolean,
    val reminderTime: String,
    val themeMode: String
)
```

---

# 5. Danh sách màn hình

## 5.1. Onboarding Screen

Mục tiêu:

- Giới thiệu app
- Cho phép người dùng bắt đầu hoặc dùng dạng guest

Thành phần UI:

- Logo / tên app MinLish Lite
- Tagline
- 3 lợi ích: Flashcards, Spaced Repetition, Track Progress
- Button Get Started
- Button Continue as Guest

Topic áp dụng:

- Compose Fundamentals: Text, Button, Column, Row, Card
- Modifier: padding, fillMaxWidth, background, size
- Basic Theming: màu chủ đạo, typography
- Reusability: tách FeatureCard component

Yêu cầu dự án liên quan:

- User Management
- Usability

---

## 5.2. Home / Dashboard Screen

Mục tiêu:

- Hiển thị tổng quan việc học
- Gợi ý học ngay
- Hiển thị từ cần ôn hôm nay

Thành phần UI:

- Greeting
- Stat summary: Words Learned, Due Today, Accuracy, Streak
- Button Study Now
- Today’s Review list
- Your Decks preview
- Bottom Navigation

Topic áp dụng:

- Compose Fundamentals: layout, card, button, text
- State & Recomposition: dashboard thay đổi khi dữ liệu Room thay đổi
- ViewModel & State Management: HomeUiState
- Local Storage: lấy số liệu từ Room
- Repository Pattern: HomeViewModel gọi repository
- Performance: dùng LazyColumn nếu nội dung dài

Yêu cầu dự án liên quan:

- Progress Tracking
- Daily Learning Plan
- Practice Module
- Usability

---

## 5.3. Deck List Screen

Mục tiêu:

- Quản lý danh sách bộ từ vựng

Chức năng:

- Xem danh sách deck
- Tìm kiếm deck
- Lọc theo tag
- Thêm deck
- Sửa deck
- Xóa deck

Thành phần UI:

- Search bar
- Filter chips
- Deck cards
- Add New Deck button
- Bottom navigation

Topic áp dụng:

- Compose Fundamentals: LazyColumn, Card, TextField, Button
- State & Recomposition: search query thay đổi thì list cập nhật
- Navigation: đi từ Deck List sang Deck Detail
- ViewModel: DeckListViewModel quản lý state
- Local Storage: CRUD Deck bằng Room
- Repository Pattern: DeckRepository
- Performance: LazyColumn cho danh sách nhiều deck

Yêu cầu dự án liên quan:

- Vocabulary Management → Tạo bộ từ vựng
- Tags
- Usability

---

## 5.4. Add / Edit Deck Screen

Mục tiêu:

- Tạo hoặc sửa bộ từ vựng

Form gồm:

- Deck name
- Description
- Tag

Topic áp dụng:

- Compose Fundamentals: TextField, Button, Column
- State: form state
- State Hoisting: form nhận state từ ViewModel
- ViewModel: validate dữ liệu
- Local Storage: insert/update DeckEntity
- Navigation: quay lại Deck List sau khi lưu

Yêu cầu dự án liên quan:

- Tạo bộ từ vựng
- Tên bộ từ
- Mô tả
- Tags

---

## 5.5. Vocabulary List / Deck Detail Screen

Mục tiêu:

- Xem danh sách từ trong một bộ từ

Chức năng:

- Xem danh sách word
- Search word
- Filter: All, Due Today, Learned
- Nghe phát âm nếu có URL/audio hoặc dùng TextToSpeech cơ bản
- Chuyển sang Add/Edit Word
- Chuyển sang Word Detail

Thành phần UI:

- Deck summary card
- Search bar
- Filter chips
- Word list
- Floating Action Button thêm từ

Topic áp dụng:

- Navigation: nhận deckId từ route
- Passing Data: truyền deckId
- StateFlow: observe danh sách word
- Local Storage: query words by deckId
- Repository: WordRepository
- Performance: LazyColumn

Yêu cầu dự án liên quan:

- Vocabulary Management
- Thêm từ vựng
- Theo dõi từ đến hạn ôn

---

## 5.6. Add / Edit Word Screen

Mục tiêu:

- Thêm hoặc sửa từ vựng
- Có thể tra từ qua API

Form gồm:

- Word
- Pronunciation
- Meaning
- Description
- Example
- Collocation
- Related words
- Note
- Level

Chức năng:

- Nhập dữ liệu thủ công
- Bấm Lookup via API để tra nghĩa
- Hiển thị loading khi gọi API
- Hiển thị error nếu lỗi mạng
- Preview dữ liệu từ API
- Lưu từ vào Room

Topic áp dụng:

- Compose Fundamentals: form UI
- State & Recomposition: loading/error/success khi lookup API
- ViewModel: AddWordViewModel
- Networking: Retrofit gọi Dictionary API
- Coroutines: gọi API trong viewModelScope
- Repository Pattern: DictionaryRepository + WordRepository
- Error Handling: xử lý lỗi API
- Local Storage: lưu WordEntity

Yêu cầu dự án liên quan:

- Word
- Pronunciation
- Meaning
- Description
- Example
- Collocation
- Related words
- Note
- Networking bổ sung để tra từ

---

## 5.7. Word Detail Screen

Mục tiêu:

- Xem chi tiết một từ

Hiển thị:

- Word
- Pronunciation
- Meaning
- Description
- Example
- Collocation
- Related words
- Note
- Review count
- Next review date

Chức năng:

- Edit word
- Delete word
- Mark as learned

Topic áp dụng:

- Navigation: nhận wordId
- ViewModel: WordDetailViewModel
- Local Storage: query word by id
- State: loading/success/error

Yêu cầu dự án liên quan:

- Vocabulary Management
- Progress Tracking

---

## 5.8. Study / Flashcard Screen

Mục tiêu:

- Học từ bằng flashcard
- Áp dụng SRS

Chức năng:

- Lấy danh sách từ cần học hoặc cần ôn
- Hiển thị front card: word
- Hiển thị back card: meaning, example, collocation
- Flip card
- Người dùng chọn Again / Hard / Good / Easy
- Tính nextReviewAt
- Ghi ReviewHistory
- Chuyển sang card tiếp theo

SRS đơn giản:

```text
Again → ôn lại hôm nay
Hard  → ôn sau 1 ngày
Good  → ôn sau 3 ngày
Easy  → ôn sau 7 ngày
```

Topic áp dụng:

- Compose Fundamentals: Card, Button, Animation cơ bản nếu có
- State & Recomposition: flip state, currentIndex, answer result
- State Hoisting: FlashcardContent stateless
- ViewModel: StudyViewModel quản lý danh sách và logic
- Coroutines: cập nhật database không block UI
- Local Storage: update nextReviewAt
- Repository Pattern: StudyRepository hoặc WordRepository
- Testing: test SRS calculation
- Performance: tránh recomposition toàn màn hình không cần thiết

Yêu cầu dự án liên quan:

- Flashcard Learning
- Spaced Repetition
- Again / Hard / Good / Easy
- Next review time
- Ease factor có thể làm đơn giản bằng level hoặc difficultyScore
- Practice Module

---

## 5.9. Review Today Screen

Mục tiêu:

- Hiển thị danh sách từ đến hạn ôn hôm nay

Chức năng:

- Lọc word có nextReviewAt <= hôm nay
- Bấm Study Now để học các từ này
- Hiển thị số từ cần ôn

Topic áp dụng:

- Local Storage: query theo nextReviewAt
- ViewModel: ReviewTodayViewModel
- StateFlow: tự cập nhật khi học xong
- Navigation: đi sang Study Screen với mode ReviewToday

Yêu cầu dự án liên quan:

- Daily Learning Plan
- Số từ cần ôn
- Notification System

---

## 5.10. Progress / Analytics Screen

Mục tiêu:

- Theo dõi tiến độ học

Hiển thị:

- Total words learned
- Due today
- Accuracy
- Streak
- Weekly activity
- Retention rate
- Level estimate
- Recent achievements

Cách tính đơn giản:

```text
Accuracy = correctCount / reviewCount
Retention rate = số lần chọn Good/Easy / tổng review
Streak = số ngày liên tiếp có học
Level estimate:
  < 300 words → Beginner
  300–1000 words → Intermediate
  > 1000 words → Advanced
```

Topic áp dụng:

- Compose Fundamentals: chart UI đơn giản bằng Canvas hoặc custom bar chart
- State & Recomposition: thống kê thay đổi sau mỗi lần học
- ViewModel: ProgressViewModel
- Local Storage: query ReviewHistory
- Repository Pattern: ProgressRepository
- Performance: tính toán ở repository/usecase, không tính phức tạp trong UI

Yêu cầu dự án liên quan:

- Dashboard
- Daily activity
- Retention rate
- Level estimation
- Accuracy
- Streak

---

## 5.11. Settings Screen

Mục tiêu:

- Cấu hình việc học

Chức năng:

- Số từ mới mỗi ngày
- Bật/tắt nhắc học
- Chọn giờ nhắc học
- Theme: Light/System
- Thông tin user local

Topic áp dụng:

- Compose Fundamentals: Switch, Slider, Card, Row
- State: settings state
- DataStore: lưu cấu hình nhỏ
- ViewModel: SettingsViewModel
- Local Storage: Preferences/DataStore
- Notification: lên lịch nhắc học nếu bật

Yêu cầu dự án liên quan:

- Hồ sơ người dùng
- Daily Learning Plan
- Notification System
- Usability

---

# 6. Plan triển khai theo từng phase

## Phase 1: Khởi tạo project và design system

### Công việc

- Tạo project Kotlin + Jetpack Compose
- Cài Material 3
- Thiết lập theme
- Tạo color system
- Tạo typography
- Tạo các component dùng lại:
  - AppButton
  - AppTextField
  - StatCard
  - DeckCard
  - WordListItem
  - SectionHeader
  - EmptyState
  - LoadingState
  - ErrorState

### Topic áp dụng

- Compose Fundamentals
- Composable Functions
- setContent
- Layout System
- Modifier
- Basic UI Components
- Preview
- Reusability and Component Design
- Basic Theming

### Nội dung topic cần chứng minh

- UI được xây bằng composable function
- Tách component nhỏ, không viết một màn hình quá dài
- Dùng Modifier đúng cách cho padding, size, background
- Có Preview cho component chính
- Có theme thống nhất

### Kết quả phase

- App chạy được màn hình trống
- Có theme tối giản, hiện đại
- Có bộ component chung

---

## Phase 2: Thiết lập Navigation

### Công việc

Tạo navigation graph gồm các route:

```kotlin
object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DECKS = "decks"
    const val ADD_DECK = "add_deck"
    const val DECK_DETAIL = "deck_detail/{deckId}"
    const val ADD_WORD = "add_word/{deckId}"
    const val WORD_DETAIL = "word_detail/{wordId}"
    const val STUDY = "study/{deckId}"
    const val PROGRESS = "progress"
    const val SETTINGS = "settings"
}
```

### Topic áp dụng

- Navigation
- Single Activity approach
- NavHost
- Route
- Navigation graph
- Passing Data Between Screens
- Back Stack Management

### Nội dung topic cần chứng minh

- App dùng một Activity duy nhất
- Các màn hình là composable destination
- Truyền deckId, wordId qua route
- Có bottom navigation
- Back button hoạt động đúng

### Kết quả phase

- Điều hướng được giữa các màn hình chính
- Bottom navigation hoạt động
- Có truyền tham số cơ bản

---

## Phase 3: Thiết lập Local Storage bằng Room

### Công việc

- Tạo Entity:
  - UserEntity
  - DeckEntity
  - WordEntity
  - ReviewHistoryEntity
- Tạo DAO:
  - UserDao
  - DeckDao
  - WordDao
  - ReviewHistoryDao
- Tạo AppDatabase
- Viết query:
  - getAllDecks
  - getWordsByDeck
  - getWordsDueToday
  - insertDeck
  - updateDeck
  - deleteDeck
  - insertWord
  - updateWord
  - deleteWord
  - insertReviewHistory

### Topic áp dụng

- Local Storage
- Structured Database
- Data Access Layer
- Data Modeling
- Offline-first Approach
- Error Handling

### Nội dung topic cần chứng minh

- Dữ liệu lưu local và app mở lại vẫn còn
- UI không truy cập database trực tiếp
- Có DAO riêng
- Có entity rõ ràng
- Có query lọc dữ liệu

### Kết quả phase

- Database hoạt động
- Có thể insert/query dữ liệu test
- App có nền tảng offline-first

---

## Phase 4: Repository Pattern và Dependency Injection

### Công việc

Tạo repository:

- UserRepository
- DeckRepository
- WordRepository
- StudyRepository
- ProgressRepository
- SettingsRepository
- DictionaryRepository

Tạo dependency container:

```kotlin
class AppContainer(context: Context) {
    val database = AppDatabase.getDatabase(context)
    val deckRepository = DeckRepositoryImpl(database.deckDao())
    val wordRepository = WordRepositoryImpl(database.wordDao())
}
```

### Topic áp dụng

- Repository Pattern
- Dependency Injection
- Separation of Concerns
- Single Source of Truth
- Data Flow

### Nội dung topic cần chứng minh

- ViewModel không gọi DAO trực tiếp
- Repository quản lý dữ liệu
- Có thể thay repository thật bằng fake repository khi test
- Dependencies được đưa từ ngoài vào, không tạo lung tung trong class

### Kết quả phase

- App có kiến trúc sạch hơn
- ViewModel nhận repository qua constructor hoặc factory
- Chuẩn bị tốt cho testing

---

## Phase 5: User Management local

### Công việc

- Làm Onboarding Screen
- Cho phép Continue as Guest
- Tạo user local mặc định
- Làm Settings/Profile cơ bản:
  - Name
  - Goal
  - Level

### Topic áp dụng

- Compose Fundamentals
- State & Recomposition
- ViewModel & State Management
- Local Storage
- Repository Pattern

### Nội dung topic cần chứng minh

- Form/profile có state
- Lưu user local
- UI tự cập nhật khi thay đổi profile

### Yêu cầu dự án được đáp ứng

- User Management
- Hồ sơ người dùng
- Tên
- Mục tiêu học
- Level

### Kết quả phase

- Người dùng có thể vào app dạng guest
- Có profile local cơ bản

---

## Phase 6: Vocabulary Management – Deck CRUD

### Công việc

- Làm Deck List Screen
- Làm Add/Edit Deck Screen
- Thêm deck
- Sửa deck
- Xóa deck
- Search deck
- Filter deck theo tag

### Topic áp dụng

- Compose Fundamentals
- State & Recomposition
- Navigation
- MVVM
- ViewModel & State Management
- Local Storage
- Repository Pattern
- Performance & Optimization

### Nội dung topic cần chứng minh

- Dùng LazyColumn cho danh sách deck
- Search query là state
- DeckListViewModel expose StateFlow
- Khi thêm/sửa/xóa deck, UI tự cập nhật
- Navigation sang Add/Edit Deck

### Yêu cầu dự án được đáp ứng

- Vocabulary Management
- Tạo bộ từ vựng
- Tên bộ từ
- Mô tả
- Tags

### Kết quả phase

- Quản lý được bộ từ vựng hoàn chỉnh

---

## Phase 7: Vocabulary Management – Word CRUD

### Công việc

- Làm Vocabulary List Screen
- Làm Add/Edit Word Screen
- Làm Word Detail Screen
- Thêm từ
- Sửa từ
- Xóa từ
- Search word
- Filter word theo trạng thái

### Topic áp dụng

- Compose Fundamentals
- State & Recomposition
- Navigation
- Passing Data Between Screens
- MVVM
- ViewModel
- StateFlow
- Local Storage
- Repository Pattern
- Performance

### Nội dung topic cần chứng minh

- Truyền deckId sang Vocabulary List
- Truyền wordId sang Word Detail
- Form Add/Edit Word có state rõ ràng
- WordRepository xử lý lưu dữ liệu
- LazyColumn cho danh sách từ

### Yêu cầu dự án được đáp ứng

- Thêm từ vựng
- Word
- Pronunciation
- Meaning
- Description
- Example
- Collocation
- Related words
- Note

### Kết quả phase

- Người dùng tạo được từ vựng đầy đủ thông tin

---

## Phase 8: Networking – Dictionary API

### Công việc

- Cài Retrofit
- Tạo DictionaryApiService
- Tạo DTO cho API response
- Tạo DictionaryRepository
- Trong AddWordViewModel thêm function lookupWord(word)
- UI hiển thị:
  - Loading
  - Success preview
  - Error message
- Cho phép apply kết quả API vào form

### Topic áp dụng

- Networking
- Client–Server Model
- HTTP Basics
- REST API Concept
- JSON
- API Layer Structure
- Error Handling
- Loading and Feedback
- Coroutines & Async Programming
- Repository Pattern

### Nội dung topic cần chứng minh

- Không gọi API trực tiếp từ UI
- API đi qua Repository
- Dùng coroutine để tránh block main thread
- Có loading state
- Có error state
- Parse JSON thành DTO/model

### Yêu cầu dự án được đáp ứng

- Hỗ trợ nhập pronunciation, meaning, example
- Cải thiện usability
- Có remote data source

### Kết quả phase

- Bấm Lookup via API có thể lấy dữ liệu từ API và điền vào form

---

## Phase 9: Learning Engine – Flashcard

### Công việc

- Làm Study Screen
- Load danh sách từ trong deck hoặc từ cần ôn hôm nay
- Hiển thị card mặt trước: Word
- Hiển thị mặt sau: Meaning, Example, Collocation
- Flip card
- Next card
- Progress 3/20

### Topic áp dụng

- Compose Fundamentals
- State & Recomposition
- Stateless vs Stateful Components
- State Hoisting
- ViewModel
- Unidirectional Data Flow
- Performance

### Nội dung topic cần chứng minh

- currentWord là state
- isFlipped là state
- UI tự recomposition khi flip
- StudyViewModel xử lý logic chuyển card
- Flashcard composable nên là stateless component

### Yêu cầu dự án được đáp ứng

- Learning Module
- Flashcard Learning
- Front: word
- Back: meaning + example
- Flip animation có thể làm cơ bản

### Kết quả phase

- Người dùng học từ bằng flashcard được

---

## Phase 10: Spaced Repetition System

### Công việc

- Tạo SrsCalculator
- Khi user chọn Again / Hard / Good / Easy:
  - Tính nextReviewAt
  - Cập nhật reviewCount
  - Cập nhật correctCount
  - Cập nhật level/difficulty
  - Ghi ReviewHistoryEntity

Logic đề xuất:

```kotlin
fun calculateNextReview(result: ReviewResult, now: Long): Long {
    return when (result) {
        AGAIN -> now
        HARD -> now + 1.days
        GOOD -> now + 3.days
        EASY -> now + 7.days
    }
}
```

Có thể thêm `easeFactor` đơn giản:

```text
Again → easeFactor - 0.2
Hard  → easeFactor - 0.1
Good  → giữ nguyên
Easy  → easeFactor + 0.15
```

### Topic áp dụng

- ViewModel & State Management
- Coroutines
- Local Storage
- Repository Pattern
- Testing & Debugging

### Nội dung topic cần chứng minh

- Business logic không nằm trong UI
- SRS logic tách thành class/usecase riêng
- Có unit test cho calculateNextReview
- Cập nhật database trong coroutine

### Yêu cầu dự án được đáp ứng

- Spaced Repetition
- SM-2 ở mức đơn giản
- Again / Hard / Good / Easy
- Next review time
- Ease factor

### Kết quả phase

- App có thuật toán ôn tập ngắt quãng cơ bản

---

## Phase 11: Daily Learning Plan và Review Today

### Công việc

- Trong Settings cho chọn số từ mới mỗi ngày
- Home hiển thị:
  - số từ mới đề xuất
  - số từ cần ôn hôm nay
- Tạo ReviewTodayScreen hoặc dùng Study mode ReviewToday
- Query các từ có nextReviewAt <= today

### Topic áp dụng

- Local Storage
- StateFlow
- ViewModel
- Repository Pattern
- DataStore
- Navigation

### Nội dung topic cần chứng minh

- Setting lưu bằng DataStore
- HomeViewModel đọc setting và dữ liệu từ repository
- Danh sách cần ôn tự cập nhật sau khi học

### Yêu cầu dự án được đáp ứng

- Daily Learning Plan
- Số từ mới mỗi ngày
- Số từ cần ôn

### Kết quả phase

- App biết hôm nay người dùng cần học/ôn gì

---

## Phase 12: Progress Tracking và Analytics

### Công việc

- Tính tổng số từ
- Tính số từ đã học
- Tính số từ đến hạn hôm nay
- Tính accuracy
- Tính streak
- Tính retention rate
- Tính level estimate
- Vẽ weekly activity chart đơn giản

### Topic áp dụng

- Compose Fundamentals
- State & Recomposition
- ViewModel
- Local Storage
- Repository Pattern
- Performance & Optimization

### Nội dung topic cần chứng minh

- Chart là composable riêng
- Dữ liệu thống kê đến từ repository/usecase
- Không tính toán phức tạp trực tiếp trong composable
- UI update khi ReviewHistory thay đổi

### Yêu cầu dự án được đáp ứng

- Dashboard
- Số từ đã học
- Streak
- Accuracy
- Daily activity
- Retention rate
- Level estimation

### Kết quả phase

- Có màn hình Progress đúng yêu cầu

---

## Phase 13: Notification System

### Công việc

Mức cơ bản:

- Home hiển thị số từ cần ôn hôm nay
- Nếu có từ đến hạn thì hiện nhắc học trong app

Mức khá:

- Dùng WorkManager hoặc AlarmManager để tạo local notification hằng ngày
- Settings cho bật/tắt nhắc học
- Settings cho chọn giờ nhắc

### Topic áp dụng

- Local Storage
- Coroutines
- ViewModel
- Error Handling
- Dependency Injection

### Nội dung topic cần chứng minh

- Trạng thái reminder lưu bằng DataStore
- Notification logic tách khỏi UI
- UI chỉ bật/tắt qua ViewModel

### Yêu cầu dự án được đáp ứng

- Notification System
- Nhắc học mỗi ngày
- Nhắc từ đến hạn ôn

### Kết quả phase

- Người dùng được nhắc học trong app hoặc bằng local notification

---

## Phase 14: Testing & Debugging

### Công việc

Viết test cho:

- SrsCalculator
- AddWordViewModel validate input
- DeckRepository với fake data nếu có thể
- Progress calculation

Ví dụ test SRS:

```kotlin
@Test
fun again_shouldReturnToday() { }

@Test
fun hard_shouldReturnTomorrow() { }

@Test
fun good_shouldReturnAfterThreeDays() { }

@Test
fun easy_shouldReturnAfterSevenDays() { }
```

### Topic áp dụng

- Testing & Debugging
- Unit Testing
- Arrange Act Assert
- Mocking / Fake Dependencies
- Error Handling
- Logging
- Breakpoints

### Nội dung topic cần chứng minh

- Có test logic, không chỉ test UI
- Có fake repository để test ViewModel
- Có xử lý edge case
- Có logging ở các điểm API/database khi debug

### Yêu cầu dự án được đáp ứng

- Tăng độ tin cậy
- Chứng minh code có kiểm thử

### Kết quả phase

- Có bộ unit test cơ bản
- Có thể trình bày khi vấn đáp

---

## Phase 15: Performance & Optimization

### Công việc

- Dùng LazyColumn cho danh sách deck/word
- Tách composable nhỏ
- Không truyền object quá lớn qua navigation
- Không query database trong composable
- Không gọi API trong composable
- Dùng derivedStateOf nếu cần
- Dùng remember cho state UI cục bộ
- Dùng StateFlow từ ViewModel
- Chỉ lưu state cần thiết

### Topic áp dụng

- Performance & Optimization
- Compose-specific Performance
- Avoid Unnecessary Recomposition
- State Optimization
- Lazy Loading
- Background Processing
- Memory Management
- Network Optimization

### Nội dung topic cần chứng minh

- Danh sách dài không lag
- Không block main thread
- API/database chạy background
- UI composable nhỏ và tái sử dụng
- State tối giản

### Yêu cầu dự án được đáp ứng

- Performance
- Load nhanh
- UI/UX đơn giản mà hiệu quả

### Kết quả phase

- App mượt, ít lỗi, dễ bảo trì

---

# 7. Checklist chức năng cuối cùng

## 7.1. User Management

- [ ] Continue as Guest
- [ ] Tạo user local
- [ ] Cập nhật name
- [ ] Cập nhật goal
- [ ] Cập nhật level

## 7.2. Vocabulary Management

- [ ] Tạo deck
- [ ] Sửa deck
- [ ] Xóa deck
- [ ] Tìm kiếm deck
- [ ] Lọc deck theo tag
- [ ] Thêm word
- [ ] Sửa word
- [ ] Xóa word
- [ ] Xem chi tiết word
- [ ] Search word
- [ ] Filter word

## 7.3. Learning Engine

- [ ] Flashcard front/back
- [ ] Flip card
- [ ] Again / Hard / Good / Easy
- [ ] Tính next review date
- [ ] Ghi review history
- [ ] Progress trong phiên học

## 7.4. Practice Module

- [ ] Study theo deck
- [ ] Study các từ due today
- [ ] Hiển thị kết quả sau phiên học

## 7.5. Analytics & Progress

- [ ] Words learned
- [ ] Due today
- [ ] Accuracy
- [ ] Streak
- [ ] Weekly activity
- [ ] Retention rate
- [ ] Level estimate

## 7.6. Notification System

- [ ] Hiển thị nhắc học trong Home
- [ ] Setting bật/tắt reminder
- [ ] Reminder time
- [ ] Local notification nếu đủ thời gian

## 7.7. Networking

- [ ] Lookup word via API
- [ ] Loading state
- [ ] Error state
- [ ] Success preview
- [ ] Apply result to form

## 7.8. Testing

- [ ] Test SRS
- [ ] Test validate Add Word
- [ ] Test Progress calculation
- [ ] Fake repository cho ViewModel

---

# 8. Mapping đầy đủ yêu cầu dự án với chức năng app

| Yêu cầu trong file dự án | Cách đáp ứng trong MinLish Lite |
|---|---|
| User Management | Continue as Guest, profile local, goal, level |
| Đăng ký / đăng nhập | Giả lập local guest login, không làm backend thật |
| Hồ sơ người dùng | Settings/Profile: name, goal, level |
| Vocabulary Management | Deck CRUD + Word CRUD |
| Tạo bộ từ vựng | Add/Edit Deck |
| Tags | Trường tag trong DeckEntity |
| Thêm từ vựng | Add/Edit Word |
| Word | Field word |
| Pronunciation | Field pronunciation + API lookup |
| Meaning | Field meaning |
| Description | Field description |
| Example | Field example |
| Collocation | Field collocation |
| Related words | Field relatedWords |
| Note | Field note |
| Import / Export | Đưa vào future work |
| Flashcard Learning | Study Screen |
| Front: word | Mặt trước flashcard |
| Back: meaning + example | Mặt sau flashcard |
| Flip animation | isFlipped state + animation cơ bản |
| SRS | SrsCalculator |
| Again / Hard / Good / Easy | 4 nút đánh giá |
| Next review time | nextReviewAt |
| Ease factor | difficulty/easeFactor đơn giản |
| Daily Learning Plan | newWordsPerDay + dueToday |
| Progress Tracking | Progress Screen |
| Dashboard | Home Dashboard |
| Daily activity | Weekly chart |
| Retention rate | Retention calculation |
| Level estimation | Beginner/Intermediate/Advanced |
| Notification System | In-app reminder/local notification |
| Performance | LazyColumn, coroutine, Room, optimized state |
| Security | Không lưu password; nếu có profile local thì không lưu dữ liệu nhạy cảm |
| Usability | UI tối giản, dễ dùng, ít bước |

---

# 9. Mapping đầy đủ topic Android với phase triển khai

| Topic Android | Phase áp dụng | Nội dung chứng minh |
|---|---|---|
| Compose Fundamentals | Phase 1, toàn bộ UI | Composable, Text, Button, Card, TextField, Modifier, Preview |
| State & Recomposition | Phase 5–12 | State thay đổi thì UI tự cập nhật |
| Navigation | Phase 2, 6, 7, 9 | NavHost, route, truyền deckId/wordId, back stack |
| MVVM | Phase 4 trở đi | UI → ViewModel → Repository → Data source |
| ViewModel & State Management | Phase 5–12 | UiState, StateFlow, event, loading/error/success |
| Networking | Phase 8 | Retrofit, HTTP, JSON, API layer, error handling |
| Coroutines & Async | Phase 8, 10, 12 | viewModelScope, suspend function, IO task |
| Local Storage | Phase 3, 6, 7, 10, 12 | Room, DataStore, offline-first |
| Repository Pattern | Phase 4 trở đi | Repository là single source of truth |
| Dependency Injection | Phase 4 | AppContainer hoặc Hilt |
| Testing & Debugging | Phase 14 | Unit test, fake repository, logging, breakpoint |
| Performance & Optimization | Phase 15 | LazyColumn, optimized state, avoid unnecessary recomposition |

---

# 10. Thứ tự ưu tiên nếu ít thời gian

## Mức bắt buộc để nộp ổn

1. Project + Theme + Navigation
2. Room Database
3. Deck CRUD
4. Word CRUD
5. Flashcard Study
6. SRS cơ bản
7. Home Dashboard
8. Progress Screen cơ bản

## Mức nên có để điểm tốt

9. Dictionary API
10. Settings với daily goal
11. Review Today
12. Unit test SRS
13. Local notification

## Mức mở rộng nếu còn thời gian

14. Import CSV
15. Export CSV
16. Dark mode
17. Animation đẹp hơn
18. Hilt thay Manual DI
19. UI test

---

# 11. Kịch bản demo khi bảo vệ

## Demo 1: Onboarding

- Mở app
- Bấm Continue as Guest
- Vào Home

Kiến thức nói kèm:

- Single Activity
- Navigation Compose
- Compose UI

## Demo 2: Quản lý bộ từ

- Vào Decks
- Tạo deck mới
- Sửa deck
- Xem deck list cập nhật

Kiến thức nói kèm:

- Room Database
- Repository Pattern
- ViewModel + StateFlow
- LazyColumn

## Demo 3: Thêm từ

- Vào IELTS Common
- Bấm add word
- Nhập từ
- Lookup via API
- Save word

Kiến thức nói kèm:

- Retrofit
- Coroutine
- Loading/Error/Success state
- Không gọi API trong UI

## Demo 4: Học flashcard

- Bấm Study Now
- Flip card
- Chọn Good hoặc Easy
- App chuyển sang từ tiếp theo

Kiến thức nói kèm:

- State & Recomposition
- SRS logic
- Update database
- ReviewHistory

## Demo 5: Progress

- Vào Progress
- Xem accuracy, streak, retention

Kiến thức nói kèm:

- Analytics từ local database
- UI recomposition theo state
- Performance optimization

## Demo 6: Settings

- Đổi số từ mới mỗi ngày
- Bật reminder
- Chọn giờ nhắc

Kiến thức nói kèm:

- DataStore
- Local notification
- State management

---

# 12. Kết luận

Plan này giúp MinLish Lite đáp ứng đủ yêu cầu đồ án nhưng vẫn vừa sức làm một mình. Trọng tâm không phải làm app quá lớn, mà là chứng minh bạn hiểu Android hiện đại:

- Compose UI
- State-driven UI
- Navigation
- MVVM
- ViewModel
- Repository
- Room
- Retrofit
- Coroutine
- DataStore
- Testing
- Performance

Nếu làm đúng theo plan này, app sẽ đủ tính năng, dễ giải thích khi vấn đáp, và không bị quá tải như một sản phẩm thương mại hoàn chỉnh.

