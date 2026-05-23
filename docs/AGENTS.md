---
name: minlish_android_agent
description: Concise Android AI agent context for the MinLish Lite project
---

You are an expert Android development agent for **MinLish Lite**.

## Project summary

MinLish Lite is an Android vocabulary learning app for a basic Android programming course.

The app helps users:

- Create vocabulary decks
- Add and manage words
- Learn with flashcards
- Review words using spaced repetition
- Track learning progress
- Receive daily review reminders
- Look up word details through a Dictionary API

Keep the project **simple enough for one student to build and explain**, but complete enough to demonstrate modern Android fundamentals.

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- MVVM
- ViewModel
- StateFlow
- Coroutines
- Room Database
- DataStore
- Retrofit
- Repository Pattern
- Manual Dependency Injection by default
- JUnit for unit tests

Use Hilt, Firebase, Google Login, or a real backend only if explicitly requested.

## Important project docs

Always read and follow:

- `design.md` — visual design system and UI rules
- `AGENTS.md` — this agent context

Do not duplicate design tokens or UI rules here. If a UI decision is needed, follow `design.md`.

## Architecture rules

Use this flow:

```text
Composable UI → ViewModel → Repository → Local/Remote Data Source
```

Rules:

- UI renders state and sends events only.
- ViewModel owns screen state and handles user actions.
- Repository owns data access logic.
- Room DAO and Retrofit API must never be called directly from Composables.
- Use StateFlow for screen state.
- Use Coroutines for database and network work.
- Keep business logic outside UI.

## Suggested package structure

```text
app/src/main/java/com/example/minlishlite
├── core
├── data
│   ├── local
│   ├── remote
│   ├── mapper
│   └── repository
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── di
├── ui
│   └── theme
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

Tests:

```text
app/src/test/java/com/example/minlishlite
app/src/androidTest/java/com/example/minlishlite
```

## Core data models

The app should have these main data groups:

- User/Profile
- Deck
- Word
- ReviewHistory
- UserSettings

Word should support:

- word
- pronunciation
- meaning
- description
- example
- collocation
- related words
- note
- level
- next review date
- review count
- correct count

## Required modules

### 1. Onboarding / User

Implement lightweight local user flow:

- Continue as Guest
- Local profile
- Name
- Learning goal
- Level

Do not implement real authentication unless requested.

### 2. Deck Management

Required:

- Create deck
- Edit deck
- Delete deck
- Search deck
- Filter by tag

### 3. Word Management

Required:

- Create word
- Edit word
- Delete word
- View word detail
- Search words
- Filter by review status

### 4. Dictionary API

Required:

- Lookup word via API
- Loading state
- Error state
- Success preview
- Apply API result to word form

Networking must go through Repository.

### 5. Flashcard Study

Required:

- Study words by deck
- Show front/back flashcard
- Flip card
- Show study progress
- Review with Again / Hard / Good / Easy

### 6. Spaced Repetition

Use a simple SRS algorithm:

```text
Again → review today
Hard  → review after 1 day
Good  → review after 3 days
Easy  → review after 7 days
```

When reviewing a word:

- Update next review date
- Update review count
- Update correct count when relevant
- Save review history

SRS logic must be testable and should live in a use case or calculator class, not in UI.

### 7. Daily Review Plan

Required:

- Show due-today count
- Study due-today words
- Let user configure new words per day

### 8. Progress / Analytics

Required:

- Words learned
- Due today
- Accuracy
- Review streak
- Weekly activity
- Retention rate
- Level estimate
- Recent achievements

### 9. Settings / Reminder

Required:

- New words per day
- Reminder enabled/disabled
- Reminder time
- Theme option if practical
- Local profile info

Local notification is optional but recommended if time allows.

## Android topic coverage

The project must demonstrate:

- Compose Fundamentals: reusable composables, Modifier, layout, Preview
- State & Recomposition: state hoisting, immutable UI state, StateFlow
- Navigation: single Activity, NavHost, routes, deckId/wordId arguments
- MVVM: clear UI/ViewModel/Repository separation
- Networking: Retrofit, DTO, mapper, loading/error/success states
- Coroutines: suspend functions, viewModelScope, no main-thread blocking
- Local Storage: Room for app data, DataStore for settings
- Repository Pattern: single access point for data
- Dependency Injection: Manual DI via AppContainer unless told otherwise
- Testing: SRS, validation, progress calculation, ViewModel with fake repos
- Performance: LazyColumn, small composables, avoid unnecessary recomposition

## Implementation priority

Build in this order:

1. Project setup and `design.md` theme
2. Navigation graph
3. Room entities, DAO, database
4. Repository + Manual DI
5. Deck CRUD
6. Word CRUD
7. Dictionary API lookup
8. Flashcard study screen
9. SRS logic
10. Daily review plan
11. Progress screen
12. Settings and reminders
13. Unit tests
14. Performance cleanup
15. Documentation

## Coding standards

### Kotlin

- Prefer simple, readable code.
- Use data classes for models and UI state.
- Use sealed interfaces/classes for events and result states when useful.
- Keep functions small.
- Avoid unnecessary abstraction.

### Compose

- Keep Composables small and reusable.
- Prefer stateless reusable components.
- Pass `Modifier` as the last parameter.
- Pass state down and events up.
- Do not call database or API from Composables.
- Follow `design.md` for all UI decisions.

### ViewModel

Use a clear pattern:

```kotlin
data class ScreenUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

Expose:

```kotlin
val uiState: StateFlow<ScreenUiState>
```

Handle actions through event functions such as:

```kotlin
fun onEvent(event: ScreenEvent)
```

### Repository

Use repositories for all data access:

```kotlin
interface DeckRepository {
    fun observeDecks(): Flow<List<Deck>>
    suspend fun insertDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck)
    suspend fun deleteDeck(deckId: Int)
}
```

## Testing expectations

Write unit tests for important logic:

- SRS date calculation
- Required field validation
- Duplicate deck/word validation
- Progress calculation
- ViewModel state changes using fake repositories

Use Arrange / Act / Assert.

## Commands

Use actual Gradle commands available in the project.

Common commands:

```bash
./gradlew assembleDebug
./gradlew test
./gradlew lint
./gradlew clean
```

Windows:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat test
.\gradlew.bat lint
```

## Boundaries

### Always do

- Follow `design.md`
- Keep the app beginner-friendly
- Use MVVM and Repository Pattern
- Use Room for structured app data
- Use DataStore for settings
- Use Retrofit only through Repository
- Handle loading, empty, and error states
- Keep SRS and analytics logic testable
- Keep code easy for a student to explain

### Ask first

- Adding Firebase
- Adding Google Login
- Adding backend authentication
- Adding Hilt
- Changing the architecture
- Changing the design direction
- Adding major new libraries
- Removing agreed features

### Never do

- Never put business logic in Composables
- Never call DAO/API directly from Composables
- Never use GlobalScope
- Never block the main thread
- Never store secrets or passwords in plain text
- Never hardcode API keys
- Never over-engineer the project
- Never duplicate large parts of `design.md` here

## Definition of done

A feature is done when:

- It follows `design.md`
- UI is state-driven
- ViewModel handles screen logic
- Repository handles data access
- Loading/empty/error states are covered when relevant
- Navigation works
- Important logic is testable
- The app builds successfully
- The feature is simple enough to explain during project defense
