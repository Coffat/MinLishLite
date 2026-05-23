package com.example.minlishlite

import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.model.ReviewHistory
import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.domain.repository.DeckRepository
import com.example.minlishlite.domain.repository.ProgressRepository
import com.example.minlishlite.domain.repository.SettingsRepository
import com.example.minlishlite.domain.repository.UserRepository
import com.example.minlishlite.domain.repository.WordRepository
import com.example.minlishlite.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_newWordsSuggested_isMinOfUnreviewedAndDailyGoal() = runTest {
        val viewModel = HomeViewModel(
            userRepository = FakeUserRepository(),
            settingsRepository = FakeSettingsRepository(newWordsPerDay = 10),
            progressRepository = FakeProgressRepository(dueCount = 2),
            wordRepository = FakeWordRepository(unreviewedCount = 20),
            deckRepository = FakeDeckRepository()
        )

        val state = viewModel.uiState.filter { !it.isLoading }.first()
        assertEquals(10, state.newWordsSuggested)
        assertEquals(2, state.dueTodayCount)
    }

    @Test
    fun uiState_dueTodayCount_updatesWhenDueWordsChange() = runTest {
        val dueWordsFlow = MutableStateFlow<List<Word>>(emptyList())
        val dueCountFlow = MutableStateFlow(0)
        val viewModel = HomeViewModel(
            userRepository = FakeUserRepository(),
            settingsRepository = FakeSettingsRepository(),
            progressRepository = FakeProgressRepository(dueCountFlow = dueCountFlow),
            wordRepository = FakeWordRepository(
                unreviewedCount = 0,
                dueWordsFlow = dueWordsFlow
            ),
            deckRepository = FakeDeckRepository()
        )

        dueWordsFlow.value = listOf(
            createWord(id = 1, deckId = 1),
            createWord(id = 2, deckId = 1)
        )
        dueCountFlow.value = 2

        val state = viewModel.uiState.filter { !it.isLoading }.first()
        assertEquals(2, state.dueTodayCount)
        assertEquals(2, state.dueWordsPreview.size)
    }

    private fun createWord(id: Int, deckId: Int): Word = Word(
        id = id,
        deckId = deckId,
        word = "test",
        pronunciation = "",
        meaning = "nghĩa",
        description = "",
        example = "",
        collocation = "",
        relatedWords = "",
        note = "",
        level = "A1",
        nextReviewAt = 0L,
        createdAt = 0L,
        updatedAt = 0L
    )

    private class FakeUserRepository : UserRepository {
        override fun observeUser(): Flow<User?> = flowOf(
            User(id = 1, name = "Minh", email = null, goal = "", level = "Beginner", createdAt = 0L)
        )

        override suspend fun saveUser(user: User) = Unit
    }

    private class FakeSettingsRepository(
        private val newWordsPerDay: Int = 10
    ) : SettingsRepository {
        override fun observeNewWordsPerDay(): Flow<Int> = flowOf(newWordsPerDay)
        override suspend fun saveNewWordsPerDay(count: Int) = Unit
        override fun observeReminderEnabled(): Flow<Boolean> = flowOf(true)
        override suspend fun saveReminderEnabled(enabled: Boolean) = Unit
        override fun observeReminderTime(): Flow<String> = flowOf("09:00")
        override suspend fun saveReminderTime(time: String) = Unit
    }

    private class FakeProgressRepository(
        private val dueCount: Int = 0,
        private val dueCountFlow: MutableStateFlow<Int>? = null
    ) : ProgressRepository {
        override fun observeTotalWordsLearned(): Flow<Int> = flowOf(5)

        override fun observeDueTodayCount(currentTime: Long): Flow<Int> =
            dueCountFlow ?: flowOf(dueCount)

        override fun observeReviewHistory(): Flow<List<ReviewHistory>> = flowOf(emptyList())

        override fun observeProgressAnalytics(currentTime: Long) =
            flowOf(
                com.example.minlishlite.domain.model.ProgressAnalytics(
                    totalWords = 0,
                    wordsLearned = 5,
                    dueToday = dueCount,
                    accuracyPercent = 0,
                    streakDays = 0,
                    retentionPercent = 0,
                    levelLabel = com.example.minlishlite.domain.usecase.ProgressCalculator.LEVEL_BEGINNER,
                    weeklyActivity = emptyList(),
                    achievements = emptyList()
                )
            )
    }

    private class FakeWordRepository(
        private val unreviewedCount: Int = 0,
        private val dueWordsFlow: Flow<List<Word>> = flowOf(emptyList())
    ) : WordRepository {
        override fun observeWordsByDeckId(deckId: Int): Flow<List<Word>> = flowOf(emptyList())
        override fun observeWordsDueToday(currentTime: Long): Flow<List<Word>> = dueWordsFlow
        override fun observeUnreviewedWordsCount(): Flow<Int> = flowOf(unreviewedCount)
        override suspend fun getWordById(id: Int): Word? = null
        override suspend fun insertWord(word: Word): Long = 0L
        override suspend fun updateWord(word: Word) = Unit
        override suspend fun deleteWordById(id: Int) = Unit
    }

    private class FakeDeckRepository : DeckRepository {
        override fun observeAllDecks(): Flow<List<Deck>> = flowOf(
            listOf(Deck(id = 1, name = "IELTS", description = "", tag = "IELTS", createdAt = 0L, updatedAt = 0L))
        )

        override suspend fun getDeckById(id: Int): Deck? = null
        override suspend fun insertDeck(deck: Deck): Long = 0L
        override suspend fun updateDeck(deck: Deck) = Unit
        override suspend fun deleteDeckById(id: Int) = Unit
    }
}
