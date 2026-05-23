package com.example.minlishlite.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.domain.model.User
import com.example.minlishlite.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val userExists: Boolean? = null,
    val name: String = "",
    val goal: String = "",
    val level: String = "Beginner",
    val showSetupForm: Boolean = false,
    val isSaving: Boolean = false
)

class OnboardingViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkUserExists()
    }

    private fun checkUserExists() {
        viewModelScope.launch {
            // Check if there is any user in the database
            val user = userRepository.observeUser().first()
            _uiState.update { it.copy(userExists = user != null) }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onGoalChange(goal: String) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun onLevelChange(level: String) {
        _uiState.update { it.copy(level = level) }
    }

    fun onGetStartedClick() {
        _uiState.update { it.copy(showSetupForm = true) }
    }

    fun onContinueAsGuest(onSuccess: () -> Unit) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val guestUser = User(
                id = 1,
                name = "Guest",
                email = null,
                goal = "Learn 10 words daily",
                level = "Beginner",
                createdAt = System.currentTimeMillis()
            )
            userRepository.saveUser(guestUser)
            _uiState.update { it.copy(isSaving = false, userExists = true) }
            onSuccess()
        }
    }

    fun onSaveProfile(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank() || state.goal.isBlank() || state.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val user = User(
                id = 1,
                name = state.name.trim(),
                email = null,
                goal = state.goal.trim(),
                level = state.level,
                createdAt = System.currentTimeMillis()
            )
            userRepository.saveUser(user)
            _uiState.update { it.copy(isSaving = false, userExists = true) }
            onSuccess()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication)
                OnboardingViewModel(application.container.userRepository)
            }
        }
    }
}
