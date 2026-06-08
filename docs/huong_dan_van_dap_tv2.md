# Hướng dẫn vấn đáp — Thành viên 2 (Vũ Toàn Thắng)
### Dự án MinLishLite — Nhóm 21

> **Dành cho ai:** Người chưa biết gì, cần học trong ~3 tiếng để tự tin trả lời mọi câu hỏi lý thuyết, code và vấn đáp.
>
> **Cách dùng:** Đọc từng phần theo thứ tự. Mỗi khái niệm đều có **"Nói đơn giản là..."** để hiểu nhanh, rồi mới đến code thực tế.

---

# MỤC LỤC NHANH

| Phần | Nội dung | Thời gian đọc |
|---|---|---|
| [Phần 0](#phần-0--ứng-dụng-làm-gì-và-em-làm-phần-nào) | Ứng dụng làm gì? Em phụ trách phần nào? | 5 phút |
| [Phần 1](#phần-1--lý-thuyết-android--compose-theo-từng-chủ-đề) | Lý thuyết Android & Compose (A–L) | 60 phút |
| [Phần 2](#phần-2--kiến-trúc-và-luồng-dữ-liệu) | Kiến trúc MVVM + luồng dữ liệu | 15 phút |
| [Phần 3](#phần-3--presentation-layer-10-file-ui-của-tv2) | Presentation Layer — 10 file UI | 25 phút |
| [Phần 4](#phần-4--data-layer-9-file) | Data Layer — 9 file | 20 phút |
| [Phần 5](#phần-5--coreutils-6-file) | Core/Utils — 6 file | 20 phút |
| [Phần 6](#phần-6--shared--cross-domain) | File Shared + file TV1 TV2 cần biết | 10 phút |
| [Phần 7](#phần-7--unit-tests--instrumented-tests) | Tests — 8 file | 15 phút |
| [Phần 8](#phần-8--câu-hỏi-vấn-đáp--gợi-ý-trả-lời) | 20 câu hỏi vấn đáp + gợi ý trả lời | 30 phút |

**Tổng: ~3 tiếng**

---

# PHẦN 0 — Ứng dụng làm gì và em làm phần nào?

## Ứng dụng MinLishLite là gì?

MinLishLite là **app học từ vựng tiếng Anh** trên Android, tương tự Duolingo nhưng đơn giản hơn. App có tính năng:

1. **Quản lý từ vựng** — thêm/xóa/sửa từ, phân bộ từ (deck)
2. **Học bằng flashcard** — lật thẻ, xem nghĩa, đánh giá mức độ nhớ
3. **SRS (Spaced Repetition)** — hệ thống nhắc ôn thông minh, từ nào sắp quên thì nhắc ôn
4. **Thống kê tiến trình** — streak, độ chính xác, thành tích
5. **Nhắc nhở hằng ngày** — gửi notification đúng giờ người dùng chọn

## Em (TV2) phụ trách phần nào?

```
┌─────────────────── TOÀN BỘ ỨNG DỤNG ──────────────────────┐
│                                                             │
│   ┌─────────────── TV1 phụ trách ─────────────────┐        │
│   │  • Quản lý từ (thêm/sửa/xóa)                  │        │
│   │  • Tìm kiếm, nhập CSV                          │        │
│   │  • WordEntity, DeckEntity, WordDao, DeckDao    │        │
│   │  • Onboarding, Settings (UI cơ bản)            │        │
│   │  • API từ điển (dictionaryapi.dev)              │        │
│   └───────────────────────────────────────────────┘        │
│                                                             │
│   ┌─────────────── TV2 (EM) phụ trách ────────────┐        │
│   │  • Học flashcard (StudyScreen)                  │        │
│   │  • Thuật toán SRS (SrsCalculator)               │        │
│   │  • Thống kê tiến trình (ProgressScreen)         │        │
│   │  • Notification nhắc học (WorkManager)          │        │
│   │  • Phát âm thanh (PronunciationAudioPlayer)     │        │
│   │  • Database: ReviewHistory, UserEntity          │        │
│   └───────────────────────────────────────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

---

# PHẦN 1 — Lý thuyết Android & Compose theo từng chủ đề

> Đây là phần lý thuyết thầy **rất hay hỏi**. Đọc kỹ phần này trước.

---

## A. Jetpack Compose là gì?

### Nói đơn giản là...
Ngày xưa Android dùng XML để tạo giao diện (kiểu viết file layout.xml riêng, rồi Java/Kotlin riêng). Compose bỏ XML đi, **viết UI bằng Kotlin luôn**. UI trở thành **hàm Kotlin** thay vì file XML.

### Hai cách tiếp cận
| Cũ (XML/View) | Mới (Jetpack Compose) |
|---|---|
| UI là file XML tĩnh | UI là hàm Kotlin (`@Composable`) |
| Cập nhật UI thủ công: `textView.text = "..."` | UI tự cập nhật khi state thay đổi |
| Nhiều boilerplate code | Ít code hơn, dễ đọc hơn |
| Khó tái sử dụng | Dễ tái sử dụng (gọi hàm như thường) |

### Trong project này
```kotlin
// Ví dụ: StudyScreen là một hàm Kotlin
@Composable  // <-- annotation báo cho Compose biết đây là UI
fun StudyScreen(
    studyMode: StudyMode,
    onBackClick: () -> Unit  // callback khi nhấn nút back
) {
    // code Kotlin viết UI ở đây
    Scaffold { ... }
}
```

**Declarative UI = UI là hàm của State:**
```
UI = f(State)
Khi State thay đổi → hàm chạy lại → UI tự động cập nhật
```

---

## B. State & Recomposition

### Nói đơn giản là...
**State** = dữ liệu có thể thay đổi mà UI cần hiển thị. Khi State thay đổi, Compose tự **chạy lại** (recompose) phần UI liên quan.

### Ví dụ thực tế trong project
```kotlin
// Trong StudyViewModel — State là một data class
data class StudyUiState(
    val isFlipped: Boolean = false,   // thẻ đang lật hay không
    val currentWord: WordEntity? = null,  // từ đang học
    val isLoading: Boolean = true,    // đang tải dữ liệu
    val isSessionComplete: Boolean = false  // học xong chưa
)

// State được giữ trong StateFlow (reactive container)
val uiState: StateFlow<StudyUiState> = ...

// Trong UI, collect state:
val state by viewModel.uiState.collectAsStateWithLifecycle()
// Khi state.isFlipped thay đổi → Compose tự vẽ lại Flashcard
```

### State Hoisting (kéo State lên trên)
**Nói đơn giản:** Đừng để State ẩn bên trong component con. Kéo State ra ngoài, truyền vào như tham số.

```kotlin
// SAI: State ẩn trong component
@Composable
fun Flashcard() {
    var isFlipped by remember { mutableStateOf(false) }  // State ẩn
    // khó test, khó tái sử dụng
}

// ĐÚNG: State truyền từ ngoài vào
@Composable
fun Flashcard(
    isFlipped: Boolean,   // State truyền vào
    onFlip: () -> Unit    // Event truyền ra
) { ... }
```

### Recomposition là gì?
Khi state thay đổi, Compose **chỉ chạy lại những composable bị ảnh hưởng**, không vẽ lại toàn bộ màn hình. Đây là lý do Compose hiệu quả hơn XML.

---

## C. Navigation

### Nói đơn giản là...
App có nhiều màn hình. Navigation quản lý việc **di chuyển giữa các màn hình** — giống như hệ thống "back stack" của trình duyệt web.

### Khái niệm cốt lõi
| Khái niệm | Ý nghĩa | Ví dụ trong project |
|---|---|---|
| **Route** | Tên định danh màn hình | `"study/{deckId}"`, `"progress"` |
| **NavHost** | Container chứa tất cả màn hình | Trong `AppNavigation.kt` |
| **NavController** | Điều khiển di chuyển | `navController.navigate("study/1")` |
| **Back Stack** | Danh sách màn hình đã qua | Nhấn Back → quay về màn hình trước |

### Single Activity Pattern
App chỉ có **1 Activity** (`MainActivity`), bên trong chứa **NavHost** quản lý nhiều màn hình Compose. Đây là chuẩn hiện đại của Android.

```
MainActivity
    └── NavHost (quản lý navigation)
            ├── HomeScreen (route: "home")
            ├── StudyScreen (route: "study/{deckId}")
            ├── ReviewTodayScreen (route: "review_today")
            └── ProgressScreen (route: "progress")
```

---

## D. Kiến trúc MVVM

### Nói đơn giản là...
MVVM = Model-View-ViewModel. Chia code thành 3 lớp, mỗi lớp làm đúng 1 việc:

```
┌──────────────────────────────────────────────┐
│  VIEW (UI)                                    │
│  Hiển thị dữ liệu. Gửi action lên ViewModel  │
│  StudyScreen, ProgressScreen...               │
└──────────────────┬────────────────────────────┘
                   │ observe state / send event
┌──────────────────▼────────────────────────────┐
│  VIEWMODEL                                    │
│  Xử lý logic. Giữ State. Bridge UI ↔ Data    │
│  StudyViewModel, ProgressViewModel...         │
└──────────────────┬────────────────────────────┘
                   │ call repository
┌──────────────────▼────────────────────────────┐
│  MODEL (Data Layer)                           │
│  Lưu trữ, truy xuất dữ liệu. Không biết UI   │
│  StudyRepository, AppDatabase, WordDao...     │
└───────────────────────────────────────────────┘
```

### Tại sao cần phân lớp?
- **Dễ test:** Test ViewModel không cần chạy app, test Repository không cần UI
- **Dễ bảo trì:** Thay đổi DB không ảnh hưởng UI, thay UI không ảnh hưởng logic
- **Dễ mở rộng:** Thêm tính năng mới không làm vỡ code cũ

---

## E. ViewModel & StateFlow

### Nói đơn giản là...
**ViewModel** là "bộ não" của mỗi màn hình. Nó **sống sót qua xoay màn hình** (rotation), giữ state không bị mất khi Activity bị recreate.

**StateFlow** = luồng dữ liệu reactive — giống Observable trong Java nhưng được thiết kế cho Kotlin Coroutines.

### Vì sao không dùng biến bình thường?
```kotlin
// SAI — biến bình thường
class StudyViewModel : ViewModel() {
    var isFlipped = false  // UI không tự cập nhật khi này thay đổi!
}

// ĐÚNG — StateFlow
class StudyViewModel : ViewModel() {
    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped  // UI observe cái này
}
```

### Pattern trong project
```kotlin
// Ứng dụng trong StudyViewModel:
private val _isFlipped = MutableStateFlow(false)

fun onFlipCard() {
    _isFlipped.value = true  // thay đổi state
    // → UI tự cập nhật, không cần gọi notifyDataSetChanged() hay gì cả
}

val uiState: StateFlow<StudyUiState> = combine(
    _isFlipped, _sessionWords, _currentIndex, ...
) { ... → StudyUiState(...) }
.stateIn(viewModelScope, ...)
```

### `stateIn` là gì?
Chuyển một `Flow` thông thường thành `StateFlow` — tức là luôn có giá trị hiện tại (không cần đợi emit đầu tiên).

---

## F1. Networking (Mạng)

### Trong project này dùng gì?
App gọi 2 API:
- `dictionaryapi.dev` — lấy phiên âm, audio phát âm
- `mymemory.translated.net` — dịch từ

### Nguyên tắc quan trọng
1. **Không được gọi API trên Main Thread** → dùng coroutine (suspend fun) với `Dispatchers.IO`
2. Dùng **Retrofit** để gọi API (thay cho viết HTTP thủ công)
3. Kết quả trả về qua **Repository**, không gọi thẳng từ ViewModel hay UI

---

## F2. Coroutines & Async

### Nói đơn giản là...
Khi app cần đọc database hoặc gọi mạng, nó **không được dừng lại** (block) màn hình. Coroutines giải quyết vấn đề này bằng cách cho phép **tạm dừng và tiếp tục** một hàm mà không block thread.

### Các khái niệm quan trọng

**suspend fun** = hàm có thể tạm dừng (không block thread, nhường cho công việc khác)
```kotlin
// Hàm này có thể dừng lại khi đợi DB, rồi tiếp tục sau
suspend fun reviewWord(wordId: Int, ...) {
    val word = wordDao.getWordById(wordId)  // đợi DB
    wordDao.updateWord(updatedWord)          // đợi DB
    // không block UI thread!
}
```

**Dispatchers** = chỉ định thread nào chạy coroutine
```kotlin
Dispatchers.Main   // UI thread — dùng cho cập nhật UI
Dispatchers.IO     // Background thread — dùng cho DB, mạng
Dispatchers.Default // CPU thread — dùng tính toán nặng
```

**viewModelScope** = scope gắn với ViewModel — khi ViewModel bị destroy, tất cả coroutine tự hủy
```kotlin
fun onRateCard(result: ReviewResult) {
    viewModelScope.launch {  // chạy trong background
        studyRepository.reviewWord(...)  // suspend fun
        // sau khi xong, tự quay về Main thread để cập nhật state
    }
}
```

**Flow** = luồng dữ liệu có thể emit nhiều giá trị theo thời gian (ngược với suspend fun chỉ trả về 1 giá trị)
```kotlin
// Flow từ Room DAO — tự động emit lại khi DB thay đổi
fun observeReviewHistory(): Flow<List<ReviewHistoryEntity>>

// Mỗi khi có review mới insert vào DB
// → Flow tự emit danh sách mới
// → ProgressRepository nhận và tính lại analytics
// → UI tự cập nhật
```

**combine** = kết hợp nhiều Flow thành 1
```kotlin
// Khi bất kỳ Flow nào thay đổi → tính lại analytics
combine(flow1, flow2, flow3) { v1, v2, v3 -> ... }
```

---

## G. Local Storage (Room Database)

### Nói đơn giản là...
**Room** là thư viện giúp dùng SQLite database trên Android dễ hơn. Thay vì viết SQL thủ công, Room cho phép dùng annotation Kotlin.

### 3 thành phần của Room

```
┌─────────────────────────────────────────────────────┐
│  Entity (Bảng dữ liệu)                              │
│  @Entity data class ReviewHistoryEntity(...)        │
│  Mỗi @Entity = 1 bảng trong SQLite                 │
└─────────────────────────────────────────────────────┘
                       ↕
┌─────────────────────────────────────────────────────┐
│  DAO (Data Access Object — cửa vào DB)              │
│  @Dao interface ReviewHistoryDao                    │
│  Định nghĩa các câu SQL qua annotation              │
└─────────────────────────────────────────────────────┘
                       ↕
┌─────────────────────────────────────────────────────┐
│  Database (Container chứa tất cả)                  │
│  @Database abstract class AppDatabase               │
│  Kết nối tất cả Entity và DAO lại với nhau          │
└─────────────────────────────────────────────────────┘
```

### Trong project — các bảng TV2 quản lý
| Entity | Bảng | Nội dung |
|---|---|---|
| `WordEntity` | `words` | Từ vựng + SRS fields (easeFactor, nextReviewAt) |
| `ReviewHistoryEntity` | `review_history` | Lịch sử mỗi lần ôn từ |
| `UserEntity` | `users` | Thông tin người dùng |

### Flow đặc biệt của Room
Room DAO có thể trả về `Flow<T>` thay vì giá trị thường — khi DB thay đổi, Flow tự emit lại:
```kotlin
@Query("SELECT * FROM review_history ORDER BY reviewedAt DESC")
fun observeReviewHistory(): Flow<List<ReviewHistoryEntity>>
// → Mỗi lần có review mới → Flow emit danh sách mới → UI cập nhật
```

---

## H. Repository Pattern

### Nói đơn giản là...
Repository là **"người trung gian"** giữa ViewModel và nguồn dữ liệu (DB, API). ViewModel không cần biết data đến từ đâu — chỉ gọi Repository, Repository lo phần còn lại.

```
ViewModel: "Cho tôi danh sách review history"
    → gọi StudyRepository.reviewWord(...)
        → StudyRepository quyết định: cập nhật WordDao + insert ReviewHistoryDao
ViewModel không cần biết có 2 bảng liên quan!
```

### Trong project
```kotlin
class StudyRepository(
    private val wordDao: WordDao,
    private val reviewHistoryDao: ReviewHistoryDao
) {
    suspend fun reviewWord(wordId: Int, result: ReviewResult, nextReviewAt: Long, easeFactor: Float) {
        // Cập nhật từ
        wordDao.updateWord(updatedWord)
        // Ghi lịch sử
        reviewHistoryDao.insertHistory(history)
        // ViewModel không biết chi tiết này!
    }
}
```

---

## I. Dependency Injection (DI)

### Nói đơn giản là...
**DI = "Đừng tự tạo object, hãy nhận object từ bên ngoài"**

```kotlin
// SAI — tự tạo dependency (tight coupling)
class StudyViewModel : ViewModel() {
    private val repo = StudyRepository(WordDao(), ReviewHistoryDao())
    // khó test vì không thể thay repo bằng fake
}

// ĐÚNG — nhận dependency từ bên ngoài (constructor injection)
class StudyViewModel(
    private val studyRepository: StudyRepository  // được truyền vào
) : ViewModel() {
    // Trong test có thể truyền FakeStudyRepository vào!
}
```

### Project dùng Manual DI qua AppContainer
Không dùng Hilt/Dagger. Thay vào đó, `AppContainer` tạo tất cả dependency một lần:

```kotlin
class AppContainer(context: Context) {
    val appDatabase = AppDatabase.getDatabase(context)
    val studyRepository = StudyRepository(
        wordDao = appDatabase.wordDao(),
        reviewHistoryDao = appDatabase.reviewHistoryDao()
    )
    val progressRepository = ProgressRepository(
        wordDao = appDatabase.wordDao(),
        reviewHistoryDao = appDatabase.reviewHistoryDao()
    )
    // ...
}
```

ViewModel nhận dependency qua Factory:
```kotlin
companion object {
    fun provideFactory(studyMode: StudyMode): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val app = this[APPLICATION_KEY] as MinLishApplication
            StudyViewModel(
                studyRepository = app.container.studyRepository,  // lấy từ container
                ...
            )
        }
    }
}
```

---

## K. Testing & Debugging

### Các loại test trong project

| Loại test | Chạy ở đâu | Tốc độ | File trong project |
|---|---|---|---|
| **Unit test** | JVM (máy tính) | Rất nhanh | `src/test/...` |
| **Instrumented test** | Thiết bị/Emulator | Chậm hơn | `src/androidTest/...` |

### Unit test với MockK
```kotlin
// Tạo "fake" repository bằng MockK
private val studyRepository: StudyRepository = mockk()

// Nói cho MockK biết fake sẽ trả về gì
coEvery { studyRepository.reviewWord(any(), any(), any(), any()) } returns Unit

// Gọi ViewModel, kiểm tra kết quả
viewModel.onRateCard(ReviewResult.GOOD)

// Xác nhận ViewModel đã gọi đúng
coVerify { studyRepository.reviewWord(1, ReviewResult.GOOD, any(), any()) }
```

### Test Flow với Turbine
```kotlin
viewModel.uiState.test {  // bắt đầu observe Flow
    val state = awaitItem()   // đợi emit đầu tiên
    assertEquals(false, state.isFlipped)
    
    viewModel.onFlipCard()
    val newState = awaitItem()  // đợi emit sau khi flip
    assertEquals(true, newState.isFlipped)
    
    cancelAndIgnoreRemainingEvents()
}
```

### MainDispatcherRule
Unit test không có Main thread. `MainDispatcherRule` thay `Dispatchers.Main` bằng `TestCoroutineDispatcher` để test ViewModel được.

---

## L. Performance

### Liên quan đến code TV2
- `collectAsStateWithLifecycle()` thay vì `collectAsState()` — tự dừng collect khi app vào background → tiết kiệm battery
- `SharingStarted.WhileSubscribed(5000)` — dừng Flow sau 5 giây không có subscriber → tiết kiệm tài nguyên
- `prepareAsync()` trong `PronunciationAudioPlayer` — không block UI thread khi load audio
- WorkManager `PeriodicWorkRequest` — hệ thống tự tối ưu thời điểm chạy worker để tiết kiệm battery

---

# PHẦN 2 — Kiến trúc và luồng dữ liệu

## Toàn bộ luồng khi người dùng đánh giá một từ

```
Người dùng nhấn nút "GOOD" trên StudyScreen
          │
          ▼
StudyScreen gọi: viewModel.onRateCard(ReviewResult.GOOD)
          │
          ▼
StudyViewModel.onRateCard():
    1. Lấy từ hiện tại từ _sessionWords
    2. Gọi SrsCalculator.applyReview(word.easeFactor, GOOD, now)
       → Trả về SrsReviewOutcome { nextReviewAt=..., easeFactor=2.5, isCorrect=true }
    3. Gọi studyRepository.reviewWord(wordId, GOOD, nextReviewAt, easeFactor)
          │
          ▼
    StudyRepository.reviewWord():
        a. Lấy WordEntity từ wordDao.getWordById(wordId)
        b. Tạo updatedWord = copy với reviewCount+1, correctCount+1, easeFactor mới
        c. wordDao.updateWord(updatedWord)          → Room ghi vào SQLite
        d. reviewHistoryDao.insertHistory(...)      → Room ghi lịch sử
          │
          ▼
    4. Tăng _currentIndex (sang từ tiếp theo)
    5. _isFlipped = false (lật lại mặt trước)
          │
          ▼
StateFlow uiState tự tính lại (combine)
          │
          ▼
StudyScreen nhận state mới, recompose
→ Hiển thị từ tiếp theo
```

## Luồng thống kê tiến trình

```
Khi có review mới insert vào DB
          │
          ▼ (Room Flow tự động emit)
ProgressRepository.observeProgressAnalytics()
    combine(5 Flow từ WordDao, 1 Flow từ ReviewHistoryDao)
          │
          ▼
ProgressCalculator.compute(totalWords, wordsLearned, dueToday, ...)
          │
          ▼
ProgressAnalytics { streakDays=5, accuracyPercent=80, ... }
          │
          ▼
ProgressViewModel.uiState cập nhật
          │
          ▼
ProgressScreen tự recompose → hiển thị số liệu mới
```

---

# PHẦN 3 — Presentation Layer (10 file UI của TV2)

## 3.1 `StudyScreen.kt`

**Nằm ở:** `presentation/study/StudyScreen.kt`

**Làm gì:** Màn hình học flashcard chính. Hiển thị 3 trạng thái khác nhau:

```
isLoading=true  →  Hiện vòng xoay (CircularProgressIndicator)
isSessionComplete=false  →  Hiện Flashcard + nút đánh giá
isSessionComplete=true  →  Hiện màn "Hoàn thành!" + nút về
```

**Hàm chính:**
```kotlin
@Composable
fun StudyScreen(
    studyMode: StudyMode,    // DeckDue(deckId) hoặc DueToday
    onBackClick: () -> Unit  // lambda — gọi khi nhấn nút back
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // state thay đổi → Compose tự vẽ lại
    
    when {
        state.isLoading -> LoadingState()
        state.errorMessage != null -> ErrorState(state.errorMessage)
        state.totalCount == 0 -> EmptyState("Không có từ cần ôn")
        state.isSessionComplete -> SessionCompleteContent(onBack = onBackClick)
        else -> StudyingContent(state, onFlip = viewModel::onFlipCard, onRate = viewModel::onRateCard)
    }
}
```

**Quan trọng — `collectAsStateWithLifecycle()`:**
Tương tự `collectAsState()` nhưng thông minh hơn: tự động **dừng collect** khi app vào background, tự **tiếp tục** khi app quay lại foreground → tiết kiệm tài nguyên.

---

## 3.2 `StudyViewModel.kt`

**Nằm ở:** `presentation/study/StudyViewModel.kt`

**Làm gì:** Não bộ của StudyScreen. Quản lý toàn bộ trạng thái một session học.

### State của ViewModel
```kotlin
data class StudyUiState(
    val deckName: String = "",           // tên bộ từ
    val currentWord: WordEntity? = null, // từ đang hiện
    val currentIndex: Int = 0,           // đang ở từ thứ mấy (0-based)
    val totalCount: Int = 0,             // tổng số từ trong session
    val progressLabel: String = "0/0",   // "1/5", "2/5"...
    val progressFraction: Float = 0f,    // 0.0 → 1.0 cho ProgressBar
    val isFlipped: Boolean = false,      // thẻ đã lật chưa
    val isLoading: Boolean = true,
    val isSubmittingRating: Boolean = false,  // đang lưu kết quả
    val errorMessage: String? = null,
    val isSessionComplete: Boolean = false
)
```

### Hàm quan trọng

**`onFlipCard()`:**
```kotlin
fun onFlipCard() {
    // Guard: không làm gì nếu đã xong hoặc đang lưu
    if (_isSessionComplete.value || _isSubmittingRating.value) return
    // Chỉ lật được 1 chiều (mặt trước → mặt sau, không lật lại)
    _isFlipped.update { flipped -> if (!flipped) true else flipped }
}
```

**`onRateCard(result)`:**
```kotlin
fun onRateCard(result: ReviewResult) {
    // Guard: phải đã lật thẻ mới được đánh giá
    if (!_isFlipped.value) return

    viewModelScope.launch {  // chạy trong background
        _isSubmittingRating.value = true
        val now = System.currentTimeMillis()
        // 1. Tính toán SRS
        val outcome = SrsCalculator.applyReview(word.easeFactor, result, now)
        // 2. Lưu vào DB
        studyRepository.reviewWord(word.id, result, outcome.nextReviewAt, outcome.easeFactor)
        // 3. Chuyển sang từ tiếp theo hoặc kết thúc session
        if (currentIndex < words.lastIndex) {
            _currentIndex.value = currentIndex + 1
            _isFlipped.value = false  // lật lại về mặt trước
        } else {
            _isSessionComplete.value = true
        }
        _isSubmittingRating.value = false
    }
}
```

**`provideFactory(studyMode)` — Factory Pattern:**
```kotlin
companion object {
    fun provideFactory(studyMode: StudyMode): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val app = this[APPLICATION_KEY] as MinLishApplication
            // Lấy dependencies từ AppContainer (Manual DI)
            StudyViewModel(
                deckRepository = app.container.deckRepository,
                wordRepository = app.container.wordRepository,
                studyRepository = app.container.studyRepository,
                studyMode = studyMode
            )
        }
    }
}
```

### Tại sao `_sessionWords` chỉ set một lần?
```kotlin
viewModelScope.launch {
    dueWordsFlow.collect { dueWords ->
        // Chỉ set LẦN ĐẦU khi chưa có (sessionWords == null)
        if (_sessionWords.value == null && !_isSessionComplete.value) {
            _sessionWords.value = dueWords
        }
    }
}
```
**Lý do:** Snapshot danh sách khi bắt đầu session. Nếu không làm vậy: khi ôn xong từ A, từ A bị filter ra khỏi "dueWords", danh sách thay đổi → bỏ sót từ tiếp theo.

---

## 3.3 `StudyMode.kt`

**Nằm ở:** `presentation/study/StudyMode.kt`

```kotlin
sealed class StudyMode {
    data class DeckDue(val deckId: Int) : StudyMode()
    // Ôn từ trong bộ cụ thể (từ màn DeckDetailScreen)
    // Ví dụ: StudyMode.DeckDue(deckId = 3)

    object DueToday : StudyMode()
    // Ôn tất cả từ đến hạn hôm nay (từ màn ReviewTodayScreen)
}
```

**Sealed class:** Chỉ có số lượng subtype cố định. Dùng `when (studyMode)` thì compiler sẽ cảnh báo nếu bỏ sót case.

---

## 3.4 `Flashcard.kt`

**Nằm ở:** `presentation/study/Flashcard.kt`

**Làm gì:** Component thẻ flashcard với **animation lật 3D**. Click → lật thẻ, thấy nghĩa. Có nút phát âm UK/US.

### Animation lật thẻ — cách hoạt động
```kotlin
// 1. Tạo animation: isFlipped=false → rotation=0, isFlipped=true → rotation=180
val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
)

// 2. Áp dụng lên Card
Card(
    modifier = Modifier.graphicsLayer {
        rotationY = rotation
        cameraDistance = 12f * density  // tạo hiệu ứng 3D thực sự
    }
) {
    // 3. Khi rotation > 90°, hiện mặt sau
    if (rotation <= 90f) {
        FrontFace(word)   // hiện từ tiếng Anh
    } else {
        BackFace(meaning) // hiện nghĩa tiếng Việt
    }
}
```

**`graphicsLayer`** = áp dụng biến đổi 3D lên composable mà không gây recomposition (hiệu năng cao hơn dùng Modifier.rotate()).

---

## 3.5 `ReviewRatingButtons.kt`

**Nằm ở:** `presentation/study/ReviewRatingButtons.kt`

**Làm gì:** Hiển thị 4 nút đánh giá sau khi lật thẻ.

```kotlin
@Composable
fun ReviewRatingButtons(
    onRate: (ReviewResult) -> Unit  // callback gọi lại ViewModel
) {
    Row {
        Button(onClick = { onRate(ReviewResult.AGAIN) }, color = Red)   { Text("Quên rồi") }
        Button(onClick = { onRate(ReviewResult.HARD) }, color = Orange)  { Text("Khó") }
        Button(onClick = { onRate(ReviewResult.GOOD) }, color = Green)   { Text("Ổn") }
        Button(onClick = { onRate(ReviewResult.EASY) }, color = Blue)    { Text("Dễ") }
    }
}
```

---

## 3.6 `ReviewTodayScreen.kt` & `ReviewTodayViewModel.kt`

**Nằm ở:** `presentation/review/`

**ReviewTodayScreen làm gì:** Danh sách tất cả từ đến hạn ôn hôm nay. Xem trước trước khi bắt đầu session.

**ReviewTodayViewModel — logic chính:**
```kotlin
val uiState: StateFlow<ReviewTodayUiState> = deckRepository.observeAllDecks()
    .flatMapLatest { decks ->          // khi decks thay đổi, switch sang Flow mới
        wordRepository.observeWordsDueToday(now)
            .map { dueWords ->
                // Join dueWords với decks để lấy tên bộ từ
                val deckNameById = decks.associate { it.id to it.name }
                val items = dueWords.map { word ->
                    ReviewTodayWordItem(
                        wordId = word.id,
                        word = word.word,
                        meaning = word.meaning,
                        deckName = deckNameById[word.deckId] ?: "Bộ từ"
                    )
                }
                ReviewTodayUiState(dueWords = items, dueCount = items.size, isLoading = false)
            }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReviewTodayUiState(isLoading = true))
```

**`flatMapLatest`:** Khi Flow bên ngoài (decks) emit giá trị mới, hủy Flow bên trong cũ, tạo Flow bên trong mới.

---

## 3.7 `StudyReminderBanner.kt`

**Làm gì:** Banner nhỏ trên HomeScreen nhắc user có từ cần ôn.

```kotlin
@Composable
fun StudyReminderBanner(
    dueTodayCount: Int,
    onStartReview: () -> Unit
) {
    if (dueTodayCount > 0) {  // chỉ hiện khi có từ đến hạn
        Card(onClick = onStartReview) {
            Text("Bạn có $dueTodayCount từ cần ôn hôm nay!")
        }
    }
}
```

---

## 3.8 `ProgressScreen.kt` & `ProgressViewModel.kt`

**Nằm ở:** `presentation/progress/`

**ProgressScreen hiển thị:**
- **Streak** — số ngày học liên tiếp
- **Accuracy** — % câu đúng
- **Retention** — % từ nhớ tốt (GOOD/EASY)
- **Level** — Sơ cấp / Trung cấp / Nâng cao
- **Biểu đồ hoạt động 7 ngày** — mỗi ngày ôn bao nhiêu từ
- **Thành tích (Achievements)** — huy hiệu unlock

**ProgressViewModel:**
```kotlin
val uiState: StateFlow<ProgressUiState> = progressRepository
    .observeProgressAnalytics()  // Flow tự cập nhật khi DB thay đổi
    .map { analytics ->
        ProgressUiState(analytics = analytics, isLoading = false)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressUiState(isLoading = true))
```

---

# PHẦN 4 — Data Layer (9 file)

## 4.1 `ReviewHistoryEntity.kt`

**Bảng:** `review_history`

```kotlin
@Entity(tableName = "review_history")
data class ReviewHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wordId: Int,        // từ nào được ôn
    val deckId: Int,        // thuộc bộ từ nào
    val result: String,     // "AGAIN" / "HARD" / "GOOD" / "EASY"
    val reviewedAt: Long    // thời điểm ôn (Unix timestamp ms)
)
```

**Mỗi lần ôn 1 từ = 1 bản ghi trong bảng này.** Dùng để tính streak, accuracy, retention.

---

## 4.2 `UserEntity.kt`

**Bảng:** `users`

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,  // luôn = 1 (single-user app)
    val name: String,
    val avatarUrl: String,
    val createdAt: Long
)
```

**Lưu ý:** `id` luôn là 1 vì app không hỗ trợ đa tài khoản. `@Upsert` cho phép update thay vì insert duplicate.

---

## 4.3 `ReviewHistoryDao.kt`

```kotlin
@Dao
interface ReviewHistoryDao {
    // Flow: tự emit lại khi có review mới
    @Query("SELECT * FROM review_history ORDER BY reviewedAt DESC")
    fun observeReviewHistory(): Flow<List<ReviewHistoryEntity>>

    // Thêm 1 bản ghi lịch sử
    @Insert
    suspend fun insertHistory(history: ReviewHistoryEntity)
}
```

---

## 4.4 `UserDao.kt`

```kotlin
@Dao
interface UserDao {
    // Flow: observe user liên tục
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUser(id: Int = 1): Flow<UserEntity?>

    // Đọc một lần (không reactive)
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUser(id: Int = 1): UserEntity?

    // @Upsert = INSERT or UPDATE (Room 2.5+)
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Int = 1)
}
```

**`@Upsert`:** Nếu đã có user với id=1 thì UPDATE, không thì INSERT. Tiện lợi hơn `@Insert(onConflict = REPLACE)`.

---

## 4.5 `StudyRepository.kt`

**Đây là file quan trọng nhất của TV2 trong data layer.**

```kotlin
class StudyRepository(
    private val wordDao: WordDao,
    private val reviewHistoryDao: ReviewHistoryDao
) {
    suspend fun reviewWord(
        wordId: Int,
        result: ReviewResult,
        nextReviewAt: Long,   // thời điểm ôn tiếp theo (đã tính bởi SrsCalculator)
        easeFactor: Float     // hệ số độ dễ mới (đã tính bởi SrsCalculator)
    ) {
        val now = System.currentTimeMillis()
        val wordEntity = wordDao.getWordById(wordId)
        if (wordEntity == null) {
            AppLogger.e("reviewWord: word not found id=$wordId")
            return  // guard — từ không tìm thấy → dừng lại
        }

        // Immutable update — tạo bản copy mới thay vì sửa trực tiếp
        val updatedWord = wordEntity.copy(
            reviewCount = wordEntity.reviewCount + 1,
            correctCount = if (result != ReviewResult.AGAIN) {
                wordEntity.correctCount + 1  // tăng chỉ khi đúng
            } else {
                wordEntity.correctCount  // giữ nguyên nếu AGAIN
            },
            easeFactor = easeFactor,        // cập nhật từ SrsCalculator
            nextReviewAt = nextReviewAt,    // lần ôn tiếp theo
            updatedAt = now
        )
        wordDao.updateWord(updatedWord)

        // Ghi lịch sử ôn
        val history = ReviewHistoryEntity(
            wordId = wordId,
            deckId = wordEntity.deckId,
            result = result.name,  // enum → String ("GOOD")
            reviewedAt = now
        )
        reviewHistoryDao.insertHistory(history)
    }
}
```

---

## 4.6 `ProgressRepository.kt`

**Combine 6 Flow để tính analytics real-time:**

```kotlin
fun observeProgressAnalytics(currentTime: Long): Flow<ProgressAnalytics> {
    // Combine 5 Flow từ WordDao thành 1 snapshot
    val wordCounts = combine(
        wordDao.observeTotalWordCount(),           // tổng số từ
        wordDao.observeTotalWordsLearnedCount(),   // từ đã học
        wordDao.observeWordsDueTodayCount(now),    // từ đến hạn
        wordDao.observeTotalCorrectCount(),        // tổng lần đúng
        wordDao.observeTotalReviewCount()          // tổng lần ôn
    ) { total, learned, due, correct, reviews ->
        WordCountSnapshot(total, learned, due, correct, reviews)
    }

    // Combine snapshot với lịch sử ôn
    return combine(wordCounts, observeReviewHistory()) { counts, history ->
        ProgressCalculator.compute(
            totalWords = counts.totalWords,
            ...
            reviewHistory = history,
            now = currentTime
        )
    }
}
```

**Khi nào Flow emit?** Mỗi khi có thay đổi trong bất kỳ bảng nào liên quan → analytics tự tính lại.

---

## 4.7 `UserRepository.kt`

```kotlin
class UserRepository(private val userDao: UserDao) {
    fun observeUser(): Flow<UserEntity?>        // observe liên tục
    suspend fun getUser(id: Int = 1): UserEntity?  // đọc 1 lần
    suspend fun saveUser(user: UserEntity)      // tạo/cập nhật
    suspend fun deleteUser(id: Int = 1)         // xóa (dùng khi logout)
}
```

---

## 4.8 `ReviewResult.kt`

```kotlin
enum class ReviewResult {
    AGAIN,  // "Quên rồi" → ôn lại ngay (không delay)
    HARD,   // "Khó" → +1 ngày × easeFactor
    GOOD,   // "Ổn" → +3 ngày × easeFactor
    EASY    // "Dễ" → +7 ngày × easeFactor
}
```

---

## 4.9 `ProgressAnalytics.kt`

```kotlin
data class ProgressAnalytics(
    val totalWords: Int,        // tổng số từ trong app
    val wordsLearned: Int,      // số từ đã ôn ít nhất 1 lần
    val dueToday: Int,          // số từ cần ôn hôm nay
    val accuracyPercent: Int,   // % chính xác tổng thể
    val streakDays: Int,        // streak ngày học liên tiếp
    val retentionPercent: Int,  // % kết quả GOOD/EASY
    val levelLabel: String,     // "Sơ cấp"/"Trung cấp"/"Nâng cao"
    val weeklyActivity: List<DayActivity>,
    val achievements: List<Achievement>
)

data class DayActivity(val label: String, val reviewCount: Int)
// label = "T2", "T3"...; reviewCount = số từ ôn ngày đó

data class Achievement(val title: String, val description: String, val unlocked: Boolean)
```

---

# PHẦN 5 — Core/Utils (6 file)

## 5.1 `SrsCalculator.kt` — **File quan trọng nhất của TV2**

**Nằm ở:** `core/util/SrsCalculator.kt`

### SRS là gì?
**Spaced Repetition System** = học từ vựng bằng cách **ôn đúng lúc sắp quên**. Thay vì ôn đều đặn mỗi ngày, SRS tính toán khi nào bạn sắp quên từ đó → nhắc ôn đúng lúc.

### easeFactor là gì?
**easeFactor** = hệ số đo mức độ dễ nhớ của một từ.
- Mặc định: `2.5`
- Từ hay quên: easeFactor giảm → ôn thường xuyên hơn
- Từ dễ nhớ: easeFactor tăng → ôn ít lại

### Công thức tính khoảng cách ôn

```
nextReviewAt = now + (baseDays × easeFactor × 1_ngày_ms)

baseDays:
  AGAIN = 0 (ôn ngay)
  HARD  = 1 ngày
  GOOD  = 3 ngày
  EASY  = 7 ngày

Ví dụ với easeFactor = 2.5:
  HARD: 1 × 2.5 = 2.5 ngày sau
  GOOD: 3 × 2.5 = 7.5 ngày sau
  EASY: 7 × 2.5 = 17.5 ngày sau
```

### Code thực tế

```kotlin
object SrsCalculator {
    const val DEFAULT_EASE_FACTOR = 2.5f
    private const val MIN_EASE_FACTOR = 1.3f  // sàn tối thiểu

    // Tính khi nào ôn tiếp theo
    fun calculateNextReview(result: ReviewResult, now: Long, easeFactor: Float): Long {
        if (result == ReviewResult.AGAIN) return now  // ôn ngay

        val baseIntervalDays = when (result) {
            ReviewResult.HARD -> 1
            ReviewResult.GOOD -> 3
            ReviewResult.EASY -> 7
            ReviewResult.AGAIN -> 0  // đã xử lý ở trên
        }
        val intervalMillis = (baseIntervalDays * easeFactor * TimeUnit.DAYS.toMillis(1)).toLong()
        return now + intervalMillis
    }

    // Điều chỉnh easeFactor sau mỗi lần ôn
    fun adjustEaseFactor(currentEaseFactor: Float, result: ReviewResult): Float {
        val adjusted = when (result) {
            ReviewResult.AGAIN -> currentEaseFactor - 0.2f  // khó → ôn thường hơn
            ReviewResult.HARD  -> currentEaseFactor - 0.1f
            ReviewResult.GOOD  -> currentEaseFactor          // giữ nguyên
            ReviewResult.EASY  -> currentEaseFactor + 0.15f  // dễ → ôn ít lại
        }
        return adjusted.coerceAtLeast(MIN_EASE_FACTOR)  // không xuống dưới 1.3
    }

    // Hàm tổng hợp — StudyViewModel gọi cái này
    fun applyReview(currentEaseFactor: Float, result: ReviewResult, now: Long): SrsReviewOutcome {
        val newEaseFactor = adjustEaseFactor(currentEaseFactor, result)
        return SrsReviewOutcome(
            nextReviewAt = calculateNextReview(result, now, newEaseFactor),
            easeFactor = newEaseFactor,
            isCorrect = result != ReviewResult.AGAIN
        )
    }
}
```

### Ví dụ cụ thể
```
Từ "apple" — easeFactor hiện tại: 2.5

Người dùng nhấn EASY:
  adjustEaseFactor(2.5, EASY) = 2.5 + 0.15 = 2.65
  calculateNextReview(EASY, now, 2.65) = now + 7 × 2.65 ngày = 18.55 ngày sau
  → SrsReviewOutcome { nextReviewAt=now+18.55ngày, easeFactor=2.65, isCorrect=true }

Người dùng nhấn AGAIN:
  adjustEaseFactor(2.5, AGAIN) = 2.5 - 0.2 = 2.3
  calculateNextReview(AGAIN, now, 2.3) = now (ôn ngay)
  → SrsReviewOutcome { nextReviewAt=now, easeFactor=2.3, isCorrect=false }
```

---

## 5.2 `ProgressCalculator.kt`

**Là pure object** — chỉ có hàm thuần túy, không có side effect, dễ test.

### Các hàm quan trọng

| Hàm | Input | Logic | Output |
|---|---|---|---|
| `computeAccuracyPercent` | totalCorrect, totalReviews | (correct/reviews) × 100, clamp [0,100] | Int (%) |
| `computeRetentionPercent` | List<ReviewHistoryEntity> | % bản ghi có result=GOOD/EASY | Int (%) |
| `computeStreak` | history, today, zoneId | Đếm ngày liên tiếp có ôn (bắt từ hôm nay hoặc hôm qua) | Int (ngày) |
| `estimateLevel` | wordsLearned | <300 Sơ cấp, 300–1000 Trung cấp, >1000 Nâng cao | String |
| `buildWeeklyActivity` | history, today, zoneId | Map 7 ngày gần nhất → số lần ôn mỗi ngày | List<DayActivity> |
| `buildAchievements` | analytics | 5 thành tích với điều kiện unlock | List<Achievement> |

### Accuracy vs Retention — khác nhau thế nào?
```
Ví dụ: 10 lần ôn: GOOD, EASY, HARD, GOOD, AGAIN, EASY, GOOD, HARD, GOOD, EASY

Accuracy: đếm tất cả kết quả ≠ AGAIN → 9/10 = 90%
Retention: đếm chỉ GOOD và EASY → 6/10 = 60%

→ Accuracy cao nhưng Retention thấp: nhớ được nhưng không nhớ chắc
```

---

## 5.3 `PronunciationAudioPlayer.kt`

```kotlin
object PronunciationAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, url: String) {
        stop()  // dừng audio đang phát trước (tránh 2 audio cùng lúc)
        if (url.isBlank()) return

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_SPEECH)
                    .setUsage(USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            setOnPreparedListener { it.start() }   // khi sẵn sàng → phát
            setOnCompletionListener { stop() }      // khi xong → cleanup
            prepareAsync()  // QUAN TRỌNG: async, không block UI thread
        }
    }

    fun stop() {
        mediaPlayer?.release()  // giải phóng tài nguyên
        mediaPlayer = null
    }
}
```

**Tại sao `prepareAsync()` thay vì `prepare()`?**
`prepare()` block thread hiện tại cho đến khi audio load xong. Nếu gọi trên UI thread → app freeze. `prepareAsync()` load ở background, gọi callback `onPrepared` khi xong.

---

## 5.4 `PronunciationHelper.kt`

```kotlin
object PronunciationHelper {
    // Bỏ dấu / ở đầu/cuối chuỗi phonetic
    fun sanitizePhoneticInput(input: String): String {
        var result = input.trim()
        if (result.startsWith("/")) result = result.substring(1)
        if (result.endsWith("/")) result = result.substring(0, result.length - 1)
        return result.trim()
    }

    // Ghép UK và US thành chuỗi hiển thị
    // sanitize("/ˈæp.əl/", "/ˈæp.l/") → "UK /ˈæp.əl/ • US /ˈæp.l/"
    fun buildCombinedPronunciation(uk: String, us: String): String { ... }

    // Fallback logic: dùng pronunciation chung nếu thiếu UK/US riêng
    fun resolvePronunciationFields(...): ResolvedPronunciations { ... }
}
```

---

## 5.5 `StudyReminderWorker.kt` — File mới TV2 bổ sung

**Nằm ở:** `core/worker/StudyReminderWorker.kt`

### WorkManager là gì?
Thư viện Android để chạy **tác vụ nền đảm bảo** — đảm bảo chạy dù app đóng, dù thiết bị reboot. Khác với Thread/Coroutine: nếu app bị kill → WorkManager vẫn chạy lại sau.

### CoroutineWorker là gì?
Loại Worker cho phép dùng `suspend fun` bên trong `doWork()` — chạy trong coroutine, không block thread.

```kotlin
class StudyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {  // kế thừa CoroutineWorker

    override suspend fun doWork(): Result {
        // WorkManager gọi hàm này đúng giờ đã lên lịch
        sendStudyReminderNotification(applicationContext)
        return Result.success()  // báo hoàn thành
    }

    companion object {
        const val CHANNEL_ID = "study_reminder_channel"
        private const val NOTIFICATION_ID = 1001

        fun sendStudyReminderNotification(context: Context) {
            // Tạo Intent mở MainActivity khi nhấn notification
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, openAppIntent,
                PendingIntent.FLAG_IMMUTABLE  // bắt buộc Android 12+
            )

            // Xây dựng notification
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Đến giờ ôn từ vựng rồi! 📚")
                .setContentText("Bạn có từ cần ôn hôm nay. Học vài phút để không quên nhé!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)  // tự đóng khi nhấn
                .build()

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_ID, notification)
        }
    }
}
```

---

## 5.6 `StudyReminderScheduler.kt` — File mới TV2 bổ sung

**Nằm ở:** `core/worker/StudyReminderScheduler.kt`

```kotlin
object StudyReminderScheduler {
    private const val WORK_NAME = "study_reminder_daily"  // ID duy nhất

    fun schedule(context: Context, hour: Int, minute: Int) {
        val initialDelayMs = calculateInitialDelay(hour, minute)
        // Tạo request: chạy mỗi 24h
        val workRequest = PeriodicWorkRequestBuilder<StudyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()

        // REPLACE: nếu đã có lịch cũ thì thay mới (khi user đổi giờ)
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, workRequest)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    // Tính số ms từ bây giờ đến lần nhắc tiếp theo
    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Nếu giờ đó đã qua hôm nay → tính cho ngày mai
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    // "09:30" → Pair(9, 30)
    fun parseTime(timeString: String): Pair<Int, Int> {
        return try {
            val parts = timeString.split(":")
            Pair(parts[0].trim().toInt(), parts[1].trim().toInt())
        } catch (e: Exception) {
            Pair(9, 0)  // fallback 09:00
        }
    }
}
```

### Luồng hoàn chỉnh của Notification
```
User bật switch "Nhắc học hằng ngày" trong Settings
    │
    ▼
SettingsViewModel.onReminderEnabledChange(enabled = true)
    │ gọi
    ▼
StudyReminderScheduler.schedule(context, hour=9, minute=0)
    │ tính initialDelay, tạo PeriodicWorkRequest
    ▼
WorkManager.enqueueUniquePeriodicWork(...)
    │ lưu vào WorkManager DB
    ▼
[09:00 sáng hôm sau]
WorkManager gọi StudyReminderWorker.doWork()
    │
    ▼
sendStudyReminderNotification() → notification xuất hiện
    │ user nhấn vào
    ▼
PendingIntent mở MainActivity
```

---

# PHẦN 6 — Shared & Cross-domain

## File Shared (cả 2 thành viên cần nắm)

| File | Vai trò | TV2 cần hiểu điểm gì |
|---|---|---|
| `MainActivity.kt` | Activity duy nhất, host NavHost | Khai báo request `POST_NOTIFICATIONS` permission |
| `MinLishApplication.kt` | Application class — chạy khi app khởi động | Gọi `createNotificationChannel()` + `bootstrapReminder()` |
| `AppDatabase.kt` | Room DB v4 | Khai báo `ReviewHistoryDao`, `UserDao` là DAO của TV2 |
| `AppContainer.kt` | Manual DI | Khởi tạo `studyRepository`, `progressRepository`, `userRepository` |
| `NavGraph.kt` | Navigation graph toàn app | Routes: `study/{deckId}`, `review_today`, `progress` |
| `AndroidManifest.xml` | Permissions & components | **`RECEIVE_BOOT_COMPLETED`** — WorkManager reschedule sau reboot |

### `MinLishApplication.kt` — điểm quan trọng
```kotlin
class MinLishApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        createNotificationChannel()  // Tạo channel trước khi gửi notification
        bootstrapReminder()           // Khôi phục lịch nhắc khi app khởi động
    }

    private fun createNotificationChannel() {
        // Android 8+ bắt buộc có channel trước khi gửi notification
        val channel = NotificationChannel(
            StudyReminderWorker.CHANNEL_ID,
            "Nhắc học từ vựng",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun bootstrapReminder() {
        // Đọc cài đặt, nếu đã bật nhắc → schedule lại
        // (cần thiết sau khi app cập nhật hoặc reboot)
    }
}
```

## File của TV1 nhưng TV2 cần giải thích được

| File | Lý do TV2 cần biết |
|---|---|
| `WordEntity.kt` | Có SRS fields: `easeFactor: Float`, `nextReviewAt: Long`, `reviewCount: Int`, `correctCount: Int` — TV2 **ghi** vào các field này qua `StudyRepository` |
| `WordDao.kt` | Có SRS queries: `observeWordsDueToday(now)`, `observeTotalWordsLearnedCount()` — TV2 dùng trong `ProgressRepository` và `ReviewTodayViewModel` |
| `WordRepository.kt` | `observeWordsDueToday(now)` dùng trong `ReviewTodayViewModel` của TV2 |
| `HomeViewModel.kt` | Dùng `ProgressRepository` của TV2 để lấy `dueTodayCount` |
| `HomeScreen.kt` | Render `StudyReminderBanner` — component Compose của TV2 |
| `SettingsViewModel.kt` | `onReminderEnabledChange()` gọi `StudyReminderScheduler` của TV2 |
| `OnboardingViewModel.kt` | Gọi `userRepository.saveUser()` của TV2 |

---

# PHẦN 7 — Unit Tests & Instrumented Tests

## Tại sao cần viết test?

- **Phát hiện bug sớm** — trước khi app đến tay người dùng
- **Refactor an toàn** — thay đổi code mà biết chắc không phá logic cũ
- **Document sống** — test nói rõ "hàm này phải làm gì"

## Unit Tests (`src/test/`)

### `SrsCalculatorTest.kt` — 11 test

```kotlin
// Kiểm tra AGAIN luôn trả về now
@Test
fun `AGAIN luon tra ve thoi diem hien tai`() {
    val now = 1000L
    assertEquals(now, SrsCalculator.calculateNextReview(ReviewResult.AGAIN, now, 2.5f))
}

// Kiểm tra công thức GOOD × easeFactor
@Test
fun `GOOD voi easeFactor 2_5 cho khoang cach 7_5 ngay`() {
    val expected = (3 * 2.5f * TimeUnit.DAYS.toMillis(1)).toLong()
    assertEquals(expected, SrsCalculator.calculateNextReview(ReviewResult.GOOD, 0L, 2.5f))
}

// Kiểm tra easeFactor không xuống dưới 1.3
@Test
fun `AGAIN giu nguong toi thieu 1_3`() {
    assertEquals(1.3f, SrsCalculator.adjustEaseFactor(1.4f, ReviewResult.AGAIN), 0.01f)
}
```

### `ProgressCalculatorTest.kt` — 5+ test
```kotlin
@Test
fun `computeAccuracyPercent returns correct value`() {
    assertEquals(0, ProgressCalculator.computeAccuracyPercent(0, 0))
    assertEquals(50, ProgressCalculator.computeAccuracyPercent(5, 10))
    assertEquals(100, ProgressCalculator.computeAccuracyPercent(10, 10))
}

@Test
fun `computeRetentionPercent calculates based on GOOD and EASY`() {
    // 2 GOOD+EASY trong 4 tổng = 50%
    val history = listOf(GOOD, EASY, HARD, AGAIN)
    assertEquals(50, ProgressCalculator.computeRetentionPercent(history))
}

@Test
fun `estimateLevel returns correct levels`() {
    assertEquals("Sơ cấp", ProgressCalculator.estimateLevel(0))
    assertEquals("Sơ cấp", ProgressCalculator.estimateLevel(299))
    assertEquals("Trung cấp", ProgressCalculator.estimateLevel(300))
    assertEquals("Nâng cao", ProgressCalculator.estimateLevel(1001))
}
```

### `StudyRepositoryTest.kt` — 2 test
```kotlin
// Test 1: từ không tồn tại → không làm gì
@Test
fun reviewWord_wordNotFound_doesNothing() = runTest {
    coEvery { wordDao.getWordById(1) } returns null
    studyRepository.reviewWord(1, ReviewResult.GOOD, 1000L, 2.5f)
    coVerify(exactly = 0) { wordDao.updateWord(any()) }  // updateWord không được gọi
}

// Test 2: từ tồn tại → cập nhật word + insert history
@Test
fun reviewWord_wordFound_updatesWordAndInsertsHistory() = runTest {
    coEvery { wordDao.getWordById(1) } returns fakeWord
    studyRepository.reviewWord(1, ReviewResult.GOOD, 2000L, 2.5f)
    coVerify { wordDao.updateWord(match { it.reviewCount == 1 && it.correctCount == 1 }) }
    coVerify { reviewHistoryDao.insertHistory(match { it.result == "GOOD" }) }
}
```

### `StudyViewModelTest.kt` — 3 test
```kotlin
// Test 1: Khởi tạo đúng state
@Test
fun init_loadsDeckAndWords() = runTest {
    viewModel.uiState.test {
        val state = awaitItem()
        assertFalse(state.isLoading)
        assertEquals("My Deck", state.deckName)
        assertEquals(2, state.totalCount)
    }
}

// Test 2: onFlipCard → isFlipped = true
@Test
fun onFlipCard_flipsCard() = runTest {
    viewModel.uiState.test {
        awaitItem()                  // state ban đầu
        viewModel.onFlipCard()
        val state = awaitItem()      // state sau khi flip
        assertTrue(state.isFlipped)
    }
}

// Test 3: onRateCard → tăng index, lật lại
@Test
fun onRateCard_movesToNextWord() = runTest {
    viewModel.uiState.test {
        awaitItem()                  // state ban đầu (index=0)
        viewModel.onFlipCard()
        awaitItem()                  // state sau flip
        viewModel.onRateCard(ReviewResult.GOOD)
        val state = awaitItem()      // state sau rate
        assertEquals(1, state.currentIndex)     // sang từ thứ 2
        assertFalse(state.isFlipped)            // lật lại mặt trước
    }
}
```

### `ReviewTodayViewModelTest.kt` — 1 test
```kotlin
@Test
fun uiState_emitsLoadingThenData() = runTest(UnconfinedTestDispatcher()) {
    viewModel.uiState.test {
        val initial = awaitItem()  // state ban đầu (có thể loading hoặc rỗng)
        decksFlow.value = listOf(DeckEntity(1, "Deck 1", ...))
        dueWordsFlow.value = listOf(fakeWord)
        val updated = awaitItem()
        assertFalse(updated.isLoading)
        assertEquals(1, updated.dueCount)
        assertEquals("Deck 1", updated.dueWords[0].deckName)
    }
}
```

### `MainDispatcherRule.kt` — Test Infrastructure
```kotlin
// Vấn đề: unit test không có Main dispatcher
// Giải pháp: rule này thay Main bằng TestCoroutineDispatcher
class MainDispatcherRule(...) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)   // thay trước khi test
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()               // khôi phục sau test
    }
}

// Dùng trong test:
@get:Rule val mainDispatcherRule = MainDispatcherRule()
```

## Instrumented Tests (`src/androidTest/`)

### `UserDaoTest.kt`
- Chạy trên thiết bị/emulator với **in-memory Room database**
- Test: upsert → observe → nhận đúng user
- Test: upsert 2 lần cùng id → UPDATE không tạo thêm
- Test: delete → observe → nhận null

### `ReviewHistoryDaoTest.kt`
- Test: insert nhiều bản ghi → `observeReviewHistory` trả về theo thứ tự `reviewedAt DESC`

---

# PHẦN 8 — Câu hỏi vấn đáp & Gợi ý trả lời

---

### Câu 1: Em tự giới thiệu về phần mình phụ trách trong dự án

**Gợi ý trả lời:**
> Em phụ trách phần **học từ vựng và theo dõi tiến trình** trong app MinLishLite. Cụ thể:
>
> **Về tính năng:** Màn hình học flashcard với animation lật thẻ 3D, thuật toán SRS giúp ôn từ thông minh, dashboard thống kê tiến trình (streak, accuracy), và hệ thống notification nhắc học hằng ngày bằng WorkManager.
>
> **Về kiến trúc:** Em áp dụng MVVM — UI trong StudyScreen/ProgressScreen collect state từ ViewModel, ViewModel gọi Repository để lấy/cập nhật dữ liệu, Repository làm việc với Room DAO. Phần logic SRS và tính toán analytics được tách ra các Calculator riêng để dễ test.

---

### Câu 2: Jetpack Compose là gì? Tại sao không dùng XML?

**Gợi ý trả lời:**
> Jetpack Compose là framework UI declarative của Android, dùng Kotlin thay vì XML để tạo giao diện.
>
> XML có vấn đề: phải maintain 2 file (XML layout + Kotlin code), cập nhật UI thủ công (`textView.text = "..."`), khó tái sử dụng component.
>
> Compose giải quyết bằng cách: UI là hàm Kotlin (`@Composable`), khi state thay đổi UI **tự động recompose**. Ví dụ: trong `StudyScreen`, em chỉ cần `val state by viewModel.uiState.collectAsStateWithLifecycle()` — mỗi khi `state.currentWord` thay đổi, Compose tự vẽ lại `Flashcard` với từ mới.

---

### Câu 3: State Hoisting là gì? Em áp dụng ở đâu?

**Gợi ý trả lời:**
> State Hoisting là kỹ thuật **kéo state lên component cha**, truyền state và event callback xuống component con. Component con trở nên stateless — chỉ nhận data và báo event.
>
> Em áp dụng trong `Flashcard`:
> ```kotlin
> // Flashcard không tự quản lý isFlipped
> fun Flashcard(isFlipped: Boolean, onFlip: () -> Unit)
> ```
> State `isFlipped` được giữ trong `StudyViewModel._isFlipped`. `StudyScreen` truyền xuống Flashcard. Khi user nhấn thẻ, Flashcard gọi `onFlip()` → event đi ngược lên ViewModel → ViewModel cập nhật state → UI tự render lại.
>
> **Lợi ích:** Flashcard dễ test (chỉ cần truyền isFlipped=true/false), dễ tái sử dụng (không phụ thuộc ViewModel).

---

### Câu 4: MVVM hoạt động như thế nào? ViewModel survive configuration change ra sao?

**Gợi ý trả lời:**
> MVVM chia code thành 3 lớp: **View** (UI hiển thị), **ViewModel** (xử lý logic, giữ state), **Model** (data layer — Repository, DAO, Entity).
>
> Khi người dùng xoay màn hình, Activity bị **recreate** — nhưng ViewModel không bị recreate vì được giữ bởi `ViewModelStore`. Data không mất, UI load lại nhanh từ state cũ.
>
> Trong app: `StudyViewModel` giữ `_sessionWords` (danh sách từ đang học). Nếu không có ViewModel, xoay màn hình giữa chừng → mất hết danh sách, phải tải lại từ đầu.

---

### Câu 5: SRS là gì? Em implement như thế nào?

**Gợi ý trả lời:**
> SRS (Spaced Repetition System) là kỹ thuật học tập: **ôn từ đúng lúc sắp quên** — không ôn quá sớm (lãng phí), không ôn quá muộn (đã quên).
>
> Em implement trong `SrsCalculator.kt` theo thuật toán đơn giản hóa từ SM-2:
>
> Mỗi từ có `easeFactor` (mặc định 2.5). Khi đánh giá:
> - HARD → ôn sau `1 × easeFactor` ngày
> - GOOD → ôn sau `3 × easeFactor` ngày  
> - EASY → ôn sau `7 × easeFactor` ngày
> - AGAIN → ôn ngay lập tức
>
> `easeFactor` tự điều chỉnh: đánh giá EASY → tăng 0.15 (ôn ít lại), AGAIN → giảm 0.2 (ôn thường hơn), không xuống dưới 1.3.
>
> `StudyViewModel.onRateCard()` gọi `SrsCalculator.applyReview()` → lấy `SrsReviewOutcome` → truyền vào `StudyRepository.reviewWord()` để lưu vào DB.

---

### Câu 6: WorkManager là gì? Khác Thread/Coroutine ở điểm nào?

**Gợi ý trả lời:**
> `Thread` và `Coroutine` chạy trong process của app — **app bị kill thì task cũng mất**. WorkManager khác ở chỗ nó **đảm bảo task sẽ chạy** — dù app bị kill, dù thiết bị reboot.
>
> WorkManager lưu task vào DB riêng của nó, hệ thống Android sẽ trigger lại khi đúng điều kiện.
>
> Em dùng `PeriodicWorkRequestBuilder<StudyReminderWorker>(24, TimeUnit.HOURS)` để lên lịch gửi notification mỗi ngày. `StudyReminderScheduler.schedule()` tính `initialDelay` đến lần nhắc đầu tiên, rồi enqueue. Permission `RECEIVE_BOOT_COMPLETED` trong Manifest cho phép WorkManager reschedule sau reboot.

---

### Câu 7: Repository Pattern là gì? Lợi ích?

**Gợi ý trả lời:**
> Repository là lớp trung gian giữa ViewModel và data source. ViewModel không gọi trực tiếp DAO hay API — chỉ gọi Repository.
>
> Ví dụ: `StudyViewModel` gọi `studyRepository.reviewWord()`. Bên trong, `StudyRepository` cập nhật `WordDao` và insert `ReviewHistoryDao` — **ViewModel không biết chi tiết có 2 bảng**.
>
> Lợi ích: dễ test (mock Repository, không cần DB thật), dễ thay đổi data source (đổi Room sang SQLDelight không ảnh hưởng ViewModel), single source of truth.

---

### Câu 8: Coroutine là gì? `suspend fun`, `Flow`, `StateFlow` khác nhau thế nào?

**Gợi ý trả lời:**
> Coroutine cho phép viết code async (bất đồng bộ) theo cách tuần tự, không block thread.
>
> - **`suspend fun`**: hàm có thể tạm dừng, chờ kết quả, rồi tiếp tục — dùng cho tác vụ 1 lần (đọc/ghi DB, gọi API). Ví dụ: `studyRepository.reviewWord(...)`.
>
> - **`Flow`**: luồng dữ liệu emit nhiều giá trị theo thời gian — dùng để observe dữ liệu liên tục. Ví dụ: `reviewHistoryDao.observeReviewHistory()` emit lại mỗi khi có insert.
>
> - **`StateFlow`**: Flow đặc biệt — luôn có giá trị hiện tại, không cần chờ emit đầu tiên — dùng cho UI state. Ví dụ: `StudyViewModel.uiState`.

---

### Câu 9: Dependency Injection là gì? App này dùng cách nào?

**Gợi ý trả lời:**
> DI = không tự tạo dependency trong class, mà nhận từ bên ngoài qua constructor.
>
> App này dùng **Manual DI** qua `AppContainer`: khi app khởi động, `MinLishApplication` tạo một `AppContainer` chứa tất cả Repository. ViewModel nhận dependency qua `ViewModelProvider.Factory` — Factory lấy từ `application.container`.
>
> Ưu điểm so với Hilt/Dagger: đơn giản, dễ hiểu cho người mới, không có annotation magic. Nhược điểm: phải tự quản lý vòng đời dependency.

---

### Câu 10: Unit test, Instrumented test khác nhau thế nào?

**Gợi ý trả lời:**
> | | Unit test | Instrumented test |
> |---|---|---|
> | Chạy ở đâu | JVM (máy tính) | Thiết bị/Emulator |
> | Tốc độ | Rất nhanh (vài giây) | Chậm (vài phút) |
> | Test gì | Logic thuần: ViewModel, Calculator | Database, UI |
> | File | `src/test/` | `src/androidTest/` |
>
> Trong project: `SrsCalculatorTest`, `StudyViewModelTest` là unit test (dùng MockK mock dependency). `UserDaoTest`, `ReviewHistoryDaoTest` là instrumented test (cần Room in-memory database thật để test).

---

### Câu 11: `combine` trong Flow là gì? Tại sao `ProgressRepository` combine 6 Flow?

**Gợi ý trả lời:**
> `combine` nhận nhiều Flow, mỗi khi **bất kỳ Flow nào** emit giá trị mới → gọi lambda để tính kết quả mới.
>
> `ProgressRepository.observeProgressAnalytics()` combine 5 Flow từ WordDao (totalWords, wordsLearned, dueToday, totalCorrect, totalReviews) + 1 Flow từ ReviewHistoryDao. Khi user vừa ôn xong từ → Room cập nhật DB → tất cả Flow liên quan emit lại → `combine` tính lại `ProgressAnalytics` → ProgressScreen tự cập nhật.
>
> Không cần polling (hỏi DB mỗi giây), không cần manual notify — mọi thứ tự động.

---

### Câu 12: `@Upsert` là gì? Khác `@Insert` ở điểm nào?

**Gợi ý trả lời:**
> `@Upsert` là annotation của Room 2.5+: nếu record với primary key đã tồn tại → UPDATE, nếu không → INSERT. Với `UserEntity` (id luôn = 1), dùng `@Upsert` để cập nhật thông tin user mà không cần check tồn tại trước.
>
> `@Insert` mặc định throw exception nếu đã tồn tại (hoặc cần `OnConflictStrategy.REPLACE` thủ công). `@Upsert` sạch hơn, không cần config.

---

### Câu 13: Tại sao dùng `collectAsStateWithLifecycle()` thay vì `collectAsState()`?

**Gợi ý trả lời:**
> `collectAsState()` collect Flow liên tục kể cả khi app vào background (user nhấn Home) → lãng phí CPU và battery.
>
> `collectAsStateWithLifecycle()` tự động **dừng collect** khi lifecycle owner (Activity/Fragment) ở trạng thái `STARTED` hoặc thấp hơn, **tiếp tục** khi quay lại `RESUMED`. An toàn hơn về memory và tiết kiệm tài nguyên.

---

### Câu 14: Streak được tính như thế nào?

**Gợi ý trả lời:**
> `ProgressCalculator.computeStreak()` lấy danh sách `ReviewHistoryEntity`, map mỗi bản ghi thành `LocalDate` (ngày ôn), bỏ duplicate → `Set<LocalDate>`.
>
> Sau đó đếm ngày liên tiếp bắt từ hôm nay: nếu hôm nay có trong Set → bắt đầu đếm từ hôm nay; nếu không nhưng hôm qua có → bắt đầu từ hôm qua (cho phép người dùng streak 1 ngày chưa ôn trong ngày hiện tại).
>
> Ví dụ: Set = {6/9, 6/8, 6/7, 6/5} → streak từ 6/9 = 3 (6/9, 6/8, 6/7 — dừng vì 6/6 thiếu).

---

### Câu 15: Tại sao `_sessionWords` snapshot một lần, không observe liên tục?

**Gợi ý trả lời:**
> Nếu observe liên tục: khi người dùng ôn xong từ A, từ A bị update `nextReviewAt` sang ngày mai, Room filter ra khỏi `observeWordsDueToday()` → Flow emit danh sách mới **không có từ A** → `_sessionWords` cập nhật → session nhảy cóc.
>
> Bằng cách snapshot khi bắt đầu session (`if (_sessionWords.value == null)`), danh sách cố định suốt session → người dùng ôn hết 10 từ không bị mất. Session mới sẽ có danh sách mới.

---

### Câu 16: Nếu muốn thêm chức năng "học từ theo thứ tự ABC", em làm thế nào?

**Gợi ý trả lời:**
> Em thêm case mới vào `StudyMode`: `data class AlphabeticDue(val deckId: Int) : StudyMode()`.
>
> Trong `StudyViewModel.dueWordsFlow`, thêm branch cho mode mới:
> ```kotlin
> is StudyMode.AlphabeticDue -> wordRepository.observeWordsByDeckId(studyMode.deckId)
>     .map { words -> words.sortedBy { it.word.lowercase() } }
> ```
>
> Không cần thay đổi `StudyRepository`, `SrsCalculator`, hay bất kỳ file UI nào — đây là lợi ích của phân lớp kiến trúc.

---

### Câu 17: MockK là gì? Dùng để làm gì trong test?

**Gợi ý trả lời:**
> MockK là thư viện tạo **fake object** trong Kotlin — dùng trong unit test để thay thế dependency thật bằng fake có thể lập trình hành vi.
>
> Ví dụ: test `StudyRepository`, em không muốn dùng Room DB thật (sẽ cần chạy trên Android). Thay vào đó:
> ```kotlin
> val wordDao: WordDao = mockk()
> coEvery { wordDao.getWordById(1) } returns fakeWord  // lập trình: khi gọi với id=1 thì trả về fakeWord
> // ... chạy test ...
> coVerify { wordDao.updateWord(any()) }  // xác nhận: hàm này đã được gọi
> ```

---

### Câu 18: Turbine là gì? Tại sao cần nó để test Flow?

**Gợi ý trả lời:**
> Flow emit giá trị không đồng bộ — nếu dùng `flow.first()` thì chỉ lấy được 1 giá trị, không test được chuỗi emit.
>
> Turbine cung cấp `flow.test { }` block — bên trong có thể `awaitItem()` đợi từng emit, `cancelAndIgnoreRemainingEvents()` kết thúc. Ví dụ: test `onFlipCard()` — đợi state ban đầu (`isFlipped=false`), gọi flip, đợi state mới (`isFlipped=true`), assert.

---

### Câu 19: Notification trên Android hoạt động thế nào? App cần xin quyền gì?

**Gợi ý trả lời:**
> Android 8+ yêu cầu tạo **NotificationChannel** trước khi gửi notification. App tạo channel khi khởi động trong `MinLishApplication.createNotificationChannel()`.
>
> Android 13+ (`targetSdk >= 33`) yêu cầu xin quyền `POST_NOTIFICATIONS` — user phải chấp nhận. Khai báo trong `AndroidManifest.xml` và request tại runtime.
>
> Sau reboot, WorkManager cần quyền `RECEIVE_BOOT_COMPLETED` để reschedule các periodic task đã đăng ký.

---

### Câu 20: Điểm yếu của code TV2 và hướng cải thiện?

**Gợi ý trả lời:**
> 1. **Chưa có test** cho `ProgressRepository`, `UserRepository`, `StudyReminderWorker`, `StudyReminderScheduler` → nên bổ sung WorkManager test utilities và mock Calendar.
>
> 2. **`PronunciationAudioPlayer` là singleton** → có thể gây memory leak nếu không gọi `stop()` đúng lúc (ví dụ khi Activity bị destroy). Cải thiện: dùng lifecycle-aware component.
>
> 3. **WorkManager accuracy** — `PeriodicWorkRequest` có thể trễ vài phút do hệ thống Android tối ưu battery. Nếu cần chính xác hơn → dùng `AlarmManager.setExactAndAllowWhileIdle()`.
>
> 4. **SRS chưa tính lịch sử dài hạn** — chỉ dựa vào lần ôn vừa rồi. SM-2 gốc dùng cả lịch sử nhiều lần → khoảng cách tăng dần (interval learning).

---

## Bảng tổng kết file

| Nhóm | Số file | File nào |
|---|---|---|
| Presentation (TV2 chính) | 10 | StudyScreen, StudyViewModel, StudyMode, Flashcard, ReviewRatingButtons, ReviewTodayScreen, ReviewTodayViewModel, StudyReminderBanner, ProgressScreen, ProgressViewModel |
| Data layer (TV2 chính) | 9 | ReviewHistoryEntity, UserEntity, ReviewHistoryDao, UserDao, StudyRepository, ProgressRepository, UserRepository, ReviewResult, ProgressAnalytics |
| Core/Utils (TV2 chính) | 6 | SrsCalculator, ProgressCalculator, PronunciationAudioPlayer, PronunciationHelper, StudyReminderWorker, StudyReminderScheduler |
| Shared (cả 2) | 8 | MainActivity, MinLishApplication, AppDatabase, AppContainer, NavGraph, Routes, BottomNavigationBar, AppLogger |
| Cross-domain cần biết | 7 | WordEntity, WordDao, WordRepository, HomeViewModel, HomeScreen, SettingsViewModel, OnboardingViewModel |
| Test files | 8 | SrsCalculatorTest, ProgressCalculatorTest, StudyRepositoryTest, StudyViewModelTest, ReviewTodayViewModelTest, MainDispatcherRule, UserDaoTest, ReviewHistoryDaoTest |
| **Tổng** | **48** | |

---

*Tài liệu ôn thi vấn đáp — Vũ Toàn Thắng (Nhóm trưởng / TV2) — Nhóm 21 — MinLishLite*
