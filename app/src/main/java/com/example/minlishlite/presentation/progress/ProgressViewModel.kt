package com.example.minlishlite.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.minlishlite.MinLishApplication
import com.example.minlishlite.domain.model.ProgressAnalytics
import com.example.minlishlite.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProgressUiState(
    val isLoading: Boolean = true,
    val analytics: ProgressAnalytics? = null,
    val errorMessage: String? = null
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = progressRepository
        .observeProgressAnalytics()
        .map { analytics ->
            ProgressUiState(
                isLoading = false,
                analytics = analytics,
                errorMessage = null
            )
        }
        .catch { throwable ->
            emit(
                ProgressUiState(
                    isLoading = false,
                    analytics = null,
                    errorMessage = throwable.message ?: "Không thể tải tiến độ"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProgressUiState(isLoading = true)
        )

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinLishApplication
                ProgressViewModel(
                    progressRepository = application.container.progressRepository
                )
            }
        }
    }
}
