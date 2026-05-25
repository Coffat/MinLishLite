package com.example.minlishlite.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.data.local.entity.UserEntity
import com.example.minlishlite.data.repository.SettingsRepository
import com.example.minlishlite.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val user: UserEntity? = null,
    val newWordsPerDay: Int = 10,
    val reminderEnabled: Boolean = true,
    val reminderTime: String = "09:00",
    val showEditProfileDialog: Boolean = false,
    val editName: String = "",
    val editGoal: String = "",
    val editLevel: String = "Beginner"
)

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeUser()
        observePreferences()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.observeUser().collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            settingsRepository.observeNewWordsPerDay().collect { count ->
                _uiState.update { it.copy(newWordsPerDay = count) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeReminderEnabled().collect { enabled ->
                _uiState.update { it.copy(reminderEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeReminderTime().collect { time ->
                _uiState.update { it.copy(reminderTime = time) }
            }
        }
    }

    fun onNewWordsPerDayChange(count: Int) {
        viewModelScope.launch {
            settingsRepository.saveNewWordsPerDay(count)
        }
    }

    fun onReminderEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveReminderEnabled(enabled)
        }
    }

    fun onReminderTimeChange(time: String) {
        viewModelScope.launch {
            settingsRepository.saveReminderTime(time)
        }
    }

    fun onShowEditProfile(show: Boolean) {
        _uiState.update { state ->
            val user = state.user
            state.copy(
                showEditProfileDialog = show,
                editName = user?.name ?: "",
                editGoal = user?.goal ?: "",
                editLevel = user?.level ?: "Beginner"
            )
        }
    }

    fun onEditNameChange(name: String) {
        _uiState.update { it.copy(editName = name) }
    }

    fun onEditGoalChange(goal: String) {
        _uiState.update { it.copy(editGoal = goal) }
    }

    fun onEditLevelChange(level: String) {
        _uiState.update { it.copy(editLevel = level) }
    }

    fun onSaveProfile() {
        val state = _uiState.value
        if (state.editName.isBlank() || state.editGoal.isBlank()) return

        viewModelScope.launch {
            val updatedUser = UserEntity(
                id = 1,
                name = state.editName.trim(),
                email = state.user?.email,
                goal = state.editGoal.trim(),
                level = state.editLevel,
                createdAt = state.user?.createdAt ?: System.currentTimeMillis()
            )
            userRepository.saveUser(updatedUser)
            _uiState.update { it.copy(showEditProfileDialog = false) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication
                SettingsViewModel(
                    userRepository = application.container.userRepository,
                    settingsRepository = application.container.settingsRepository
                )
            }
        }
    }
}
