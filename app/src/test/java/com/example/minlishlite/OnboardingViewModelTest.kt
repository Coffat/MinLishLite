package com.example.minlishlite

import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.repository.UserRepository
import com.example.minlishlite.presentation.onboarding.OnboardingViewModel
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_whenNoUserExists_setsUserExistsToFalse() = runTest {
        // Arrange (default fake repo user is null)
        
        // Act
        viewModel = OnboardingViewModel(fakeUserRepository)
        
        // Assert
        assertEquals(false, viewModel.uiState.value.userExists)
    }

    @Test
    fun init_whenUserExists_setsUserExistsToTrue() = runTest {
        // Arrange
        fakeUserRepository.setUser(
            User(1, "Existing", null, "Goal", "Beginner", 100L)
        )

        // Act
        viewModel = OnboardingViewModel(fakeUserRepository)

        // Assert
        assertEquals(true, viewModel.uiState.value.userExists)
    }

    @Test
    fun continueAsGuest_savesGuestUserAndTriggersSuccess() = runTest {
        // Arrange
        viewModel = OnboardingViewModel(fakeUserRepository)
        var successCalled = false

        // Act
        viewModel.onContinueAsGuest {
            successCalled = true
        }

        // Assert
        assertTrue(successCalled)
        assertEquals(true, viewModel.uiState.value.userExists)
        
        val savedUser = fakeUserRepository.observeUser().first()
        assertNotNull(savedUser)
        assertEquals("Guest", savedUser?.name)
        assertEquals("Beginner", savedUser?.level)
    }

    @Test
    fun saveProfile_savesUserAndTriggersSuccess() = runTest {
        // Arrange
        viewModel = OnboardingViewModel(fakeUserRepository)
        var successCalled = false

        // Act
        viewModel.onNameChange("John Doe")
        viewModel.onGoalChange("Learn 20 words")
        viewModel.onLevelChange("Advanced")
        viewModel.onSaveProfile {
            successCalled = true
        }

        // Assert
        assertTrue(successCalled)
        assertEquals(true, viewModel.uiState.value.userExists)

        val savedUser = fakeUserRepository.observeUser().first()
        assertNotNull(savedUser)
        assertEquals("John Doe", savedUser?.name)
        assertEquals("Learn 20 words", savedUser?.goal)
        assertEquals("Advanced", savedUser?.level)
    }

    @Test
    fun saveProfile_whenFieldsBlank_doesNotSave() = runTest {
        // Arrange
        viewModel = OnboardingViewModel(fakeUserRepository)
        var successCalled = false

        // Act
        viewModel.onNameChange("   ")
        viewModel.onGoalChange("")
        viewModel.onSaveProfile {
            successCalled = true
        }

        // Assert
        assertFalse(successCalled)
        assertEquals(false, viewModel.uiState.value.userExists)
        assertNull(fakeUserRepository.observeUser().first())
    }

    // Helper fake repository
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
}
