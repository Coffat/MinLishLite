# Performance — MinLish Lite

Tài liệu hỗ trợ Phase 15 (Performance & Optimization) và trình bày khi vấn đáp.

## Đã áp dụng trong project

| Chủ đề | Cách triển khai |
|---|---|
| Lazy loading danh sách | `LazyColumn` + `items(..., key = { id })` ở Deck, Word, Home, Review Today, Progress |
| Navigation nhẹ | Chỉ truyền `deckId` / `wordId` qua route — không truyền `List<Word>` hay `Deck` |
| Tách lớp dữ liệu | Room / Retrofit chỉ qua Repository; ViewModel xử lý filter; Composable không gọi DAO/API |
| UiState tối giản | `HomeUiState` dùng `DueWordItem`, `DeckPreview`; `StudyUiState` chỉ expose `currentWord` + progress, không expose cả session list |
| Tránh emit trùng | `distinctUntilChanged()` trên `DeckListViewModel` và `DeckDetailViewModel` |
| Recomposition | `derivedStateOf` cho message rỗng (Deck List/Detail) và nhánh rating (Study) |
| Lifecycle-aware collect | `collectAsStateWithLifecycle()` trên các màn hình chính |
| Composable nhỏ | `DeckSummarySection`, `WordItem`, `Flashcard`, cards Progress tách file / function riêng |
| Background work | `viewModelScope`, `Dispatchers.IO` trong Repository — không block main thread |

## Lệnh kiểm tra

```bash
./gradlew assembleDebug
./gradlew test
```

## Gợi ý khi bảo vệ

1. *"Danh sách dài dùng LazyColumn để chỉ compose item hiển thị, kèm stable key giúp Compose tái sử dụng node."*
2. *"StudyViewModel giữ list từ trong memory riêng; UI state chỉ nhận từ hiện tại — giảm recomposition khi flip card."*
3. *"Filter deck/word chạy trong ViewModel combine Flow, không tính lại trong Composable mỗi frame."*

## Debug / demo (tùy chọn)

- Android Studio **Layout Inspector** → bật "Show recompositions" khi lật flashcard hoặc gõ search.
- Logcat tag `MinLishLite` (`AppLogger`) khi tra API hoặc lỗi aggregate progress — chỉ bật ở debug build.

## Liên quan

- Kiến trúc agent: [docs/AGENTS.md](AGENTS.md)
