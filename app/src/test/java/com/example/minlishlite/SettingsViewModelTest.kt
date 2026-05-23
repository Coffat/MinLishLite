package com.example.minlishlite

import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.repository.SettingsRepository
import com.example.minlishlite.domain.repository.UserRepository
import com.example.minlishlite.presentation.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        fakeSettingsRepository = FakeSettingsRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_observesUserAndSettingsCorrectly() = runTest {
        // Arrange
        val testUser = User(1, "Test User", "test@test.com", "Goal X", "Intermediate", 100L)
        fakeUserRepository.setUser(testUser)
        fakeSettingsRepository.saveNewWordsPerDay(15)
        fakeSettingsRepository.saveReminderEnabled(false)
        fakeSettingsRepository.saveReminderTime("20:30")

        // Act
        viewModel = SettingsViewModel(fakeUserRepository, fakeSettingsRepository)

        // Assert
        assertEquals(testUser, viewModel.uiState.value.user)
        assertEquals(15, viewModel.uiState.value.newWordsPerDay)
        assertEquals(false, viewModel.uiState.value.reminderEnabled)
        assertEquals("20:30", viewModel.uiState.value.reminderTime)
    }

    @Test
    fun onNewWordsPerDayChange_updatesSettingsRepository() = runTest {
        // Arrange
        viewModel = SettingsViewModel(fakeUserRepository, fakeSettingsRepository)

        // Act
        viewModel.onNewWordsPerDayChange(25)

        // Assert
        assertEquals(25, fakeSettingsRepository.observeNewWordsPerDay().first())
    }

    @Test
    fun onReminderEnabledChange_updatesSettingsRepository() = runTest {
        // Arrange
        viewModel = SettingsViewModel(fakeUserRepository, fakeSettingsRepository)

        // Act
        viewModel.onReminderEnabledChange(false)

        // Assert
        assertEquals(false, fakeSettingsRepository.observeReminderEnabled().first())
    }

    @Test
    fun onReminderTimeChange_updatesSettingsRepository() = runTest {
        // Arrange
        viewModel = SettingsViewModel(fakeUserRepository, fakeSettingsRepository)

        // Act
        viewModel.onReminderTimeChange("12:00")

        // Assert
        assertEquals("12:00", fakeSettingsRepository.observeReminderTime().first())
    }

    @Test
    fun onShowEditProfile_setsCorrectInitialValues() = runTest {
        // Arrange
        val testUser = User(1, "Original Name", "test@test.com", "Goal Y", "Advanced", 100L)
        fakeUserRepository.setUser(testUser)
        viewModel = SettingsViewModel(fakeUserRepository, fakeSettingsRepository)

        // Act
        viewModel.onShowEditProfile(true)

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state.showEditProfileDialog)
        assertEquals("Original Name", state.editName)
        assertEquals("Goal Y", state.editGoal)
        assertEquals("Advanced", state.editLevel)
    }

    @Test
    fun onSaveProfile_updatesUserRepositoryAndClosesDialog() = runTest {
        // Arrange
        val testUser = User(1, "Original Name", "test@test.com", "Goal Y", "Advanced", 100L)
        fakeUserRepository.setUser(testUser)
        viewModel = SettingsViewModel(fakeUserRepository, fakeSettingsRepository)
        viewModel.onShowEditProfile(true)

        // Act
        viewModel.onEditNameChange("Updated Name")
        viewModel.onEditGoalChange("Updated Goal")
        viewModel.onEditLevelChange("Intermediate")
        viewModel.onSaveProfile()

        // Assert
        assertFalse(viewModel.uiState.value.showEditProfileDialog)
        
        val updatedUser = fakeUserRepository.observeUser().first()
        assertNotNull(updatedUser)
        assertEquals("Updated Name", updatedUser?.name)
        assertEquals("Updated Goal", updatedUser?.goal)
        assertEquals("Intermediate", updatedUser?.level)
    }

    // Helper fake repository implementations
    private class FakeUserRepository : UserRepository {
        private val userFlow = MutableStateFlow<User?>(null)

        override fun observeUser(): Flow<User?> = userFlow

        override suspend fun saveUser(user: User) {
            userFlow.value = user
        }

        fun setUser(user: User?) {
            userFlow.value = user
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val newWordsFlow = MutableStateFlow(10)
        private val reminderEnabledFlow = MutableStateFlow(true)
        private val reminderTimeFlow = MutableStateFlow("09:00")

        override fun observeNewWordsPerDay(): Flow<Int> = newWordsFlow
        override suspend fun saveNewWordsPerDay(count: Int) {
            newWordsFlow.value = count
        }

        override fun observeReminderEnabled(): Flow<Boolean> = reminderEnabledFlow
        override suspend fun saveReminderEnabled(enabled: Boolean) {
            reminderEnabledFlow.value = enabled
        }

        override fun observeReminderTime(): Flow<String> = reminderTimeFlow
        override suspend fun saveReminderTime(time: String) {
            reminderTimeFlow.value = time
        }
    }
}
