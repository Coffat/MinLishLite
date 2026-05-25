package com.example.minlishlite.presentation.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.data.model.ReviewResult
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.EmptyState
import com.example.minlishlite.presentation.component.ErrorState
import com.example.minlishlite.presentation.component.LoadingState
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    studyMode: StudyMode,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudyViewModel = viewModel(factory = StudyViewModel.provideFactory(studyMode))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        topBar = {
            if (!state.isLoading && state.errorMessage == null) {
                StudyTopBar(
                    deckName = state.deckName,
                    onBackClick = onBackClick
                )
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding))
            state.errorMessage != null -> ErrorState(
                message = state.errorMessage ?: "Đã xảy ra lỗi.",
                onRetryClick = onBackClick,
                modifier = Modifier.padding(innerPadding)
            )
            state.isSessionComplete -> SessionCompleteContent(
                cardsReviewed = state.totalCount,
                onFinish = onBackClick,
                modifier = Modifier.padding(innerPadding)
            )
            state.totalCount == 0 -> EmptyState(
                title = "Không có từ cần ôn",
                message = "Tất cả từ trong bộ này đã được ôn. Hãy quay lại sau.",
                icon = Icons.Outlined.MenuBook,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                actionText = "Quay lại",
                onActionClick = onBackClick
            )
            else -> StudyingContent(
                state = state,
                onFlipCard = viewModel::onFlipCard,
                onRateCard = viewModel::onRateCard,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudyTopBar(
    deckName: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = deckName.ifBlank { "Học từ" },
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Background,
            titleContentColor = OnSurface,
            navigationIconContentColor = OnSurface
        )
    )
}

@Composable
private fun StudyingContent(
    state: StudyUiState,
    onFlipCard: () -> Unit,
    onRateCard: (ReviewResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentWord = state.currentWord ?: return
    val showRatingButtons by remember(state.isFlipped, state.isSubmittingRating) {
        derivedStateOf { state.isFlipped && !state.isSubmittingRating }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        StudyProgressHeader(
            progressLabel = state.progressLabel,
            progressFraction = state.progressFraction
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Flashcard(
                word = currentWord,
                isFlipped = state.isFlipped,
                onFlip = onFlipCard,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isFlipped) {
            if (state.isSubmittingRating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(106.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (showRatingButtons) {
                ReviewRatingButtons(
                    onRate = onRateCard,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Text(
                text = "Chạm thẻ để lật, sau đó chọn mức độ ghi nhớ",
                fontSize = 13.sp,
                color = OnSurfaceMuted,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StudyProgressHeader(
    progressLabel: String,
    progressFraction: Float
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tiến độ",
                fontSize = 14.sp,
                color = OnSurfaceMuted
            )
            Text(
                text = progressLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = Primary,
            trackColor = BorderColor
        )
    }
}

@Composable
private fun SessionCompleteContent(
    cardsReviewed: Int,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hoàn thành phiên học",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Bạn đã ôn $cardsReviewed thẻ trong phiên này.",
            fontSize = 16.sp,
            color = OnSurfaceMuted
        )
        Spacer(modifier = Modifier.height(32.dp))
        AppButton(
            text = "Quay lại bộ từ",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StudyScreenPreview() {
    MinLishLiteTheme {
        StudyScreen(
            studyMode = StudyMode.DeckDue(1),
            onBackClick = {}
        )
    }
}
