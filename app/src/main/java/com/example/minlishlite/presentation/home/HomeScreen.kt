package com.example.minlishlite.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.LoadingState
import com.example.minlishlite.presentation.deck.StatBox
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.AccentOrange
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary

@Composable
fun HomeScreen(
    onNavigateToStudyDueToday: () -> Unit,
    onNavigateToReviewToday: () -> Unit,
    onNavigateToDecks: () -> Unit,
    onNavigateToDeckDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.provideFactory())
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        LoadingState(modifier = modifier.fillMaxSize())
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Xin chào, ${state.userName}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Text(
                text = "Hôm nay bạn cần ôn gì?",
                fontSize = 14.sp,
                color = OnSurfaceMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (state.dueTodayCount > 0) {
            item {
                StudyReminderBanner(
                    dueTodayCount = state.dueTodayCount,
                    onClick = onNavigateToStudyDueToday
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    label = "Cần ôn",
                    value = state.dueTodayCount.toString(),
                    color = if (state.dueTodayCount > 0) AccentOrange else OnSurfaceMuted,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Từ mới",
                    value = state.newWordsSuggested.toString(),
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Đã học",
                    value = state.wordsLearned.toString(),
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            AppButton(
                text = if (state.dueTodayCount > 0) {
                    "Học ngay (${state.dueTodayCount} từ cần ôn)"
                } else {
                    "Không có từ cần ôn hôm nay"
                },
                onClick = onNavigateToStudyDueToday,
                enabled = state.dueTodayCount > 0,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.newWordsPerDay > 0) {
                Text(
                    text = "Gợi ý: tối đa ${state.newWordsPerDay} từ mới chưa học mỗi ngày",
                    fontSize = 12.sp,
                    color = OnSurfaceMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        item {
            SectionHeaderRow(
                title = "Ôn tập hôm nay",
                actionText = if (state.dueTodayCount > 0) "Xem tất cả" else null,
                onActionClick = onNavigateToReviewToday
            )
        }

        if (state.dueWordsPreview.isEmpty()) {
            item {
                Text(
                    text = "Không có từ đến hạn ôn. Tuyệt vời!",
                    fontSize = 14.sp,
                    color = OnSurfaceMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        } else {
            items(state.dueWordsPreview, key = { it.wordId }) { item ->
                DueWordPreviewRow(item = item)
            }
        }

        item {
            SectionHeaderRow(
                title = "Bộ từ của bạn",
                actionText = "Xem tất cả",
                onActionClick = onNavigateToDecks
            )
        }

        if (state.deckPreviews.isEmpty()) {
            item {
                Text(
                    text = "Chưa có bộ từ nào. Tạo bộ từ để bắt đầu học.",
                    fontSize = 14.sp,
                    color = OnSurfaceMuted
                )
            }
        } else {
            items(state.deckPreviews, key = { it.id }) { deck ->
                DeckPreviewCard(
                    deck = deck,
                    onClick = { onNavigateToDeckDetail(deck.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    actionText: String?,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
        if (actionText != null) {
            TextButton(onClick = onActionClick) {
                Text(text = actionText, color = Primary)
            }
        }
    }
}

@Composable
private fun DueWordPreviewRow(item: DueWordItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = AccentOrange)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.word,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Text(
                    text = item.meaning,
                    fontSize = 13.sp,
                    color = OnSurfaceMuted,
                    maxLines = 1
                )
            }
            Text(
                text = item.deckName,
                fontSize = 11.sp,
                color = OnSurfaceMuted
            )
        }
    }
}

@Composable
private fun DeckPreviewCard(
    deck: DeckPreview,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = deck.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            if (deck.tag.isNotBlank()) {
                Text(
                    text = deck.tag,
                    fontSize = 12.sp,
                    color = OnSurfaceMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MinLishLiteTheme {
        HomeScreen(
            onNavigateToStudyDueToday = {},
            onNavigateToReviewToday = {},
            onNavigateToDecks = {},
            onNavigateToDeckDetail = {}
        )
    }
}
