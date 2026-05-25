package com.example.minlishlite.presentation.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.data.model.Achievement
import com.example.minlishlite.data.model.DayActivity
import com.example.minlishlite.data.model.ProgressAnalytics
import com.example.minlishlite.core.util.ProgressCalculator
import com.example.minlishlite.presentation.component.EmptyState
import com.example.minlishlite.presentation.component.ErrorState
import com.example.minlishlite.presentation.component.LoadingState
import com.example.minlishlite.presentation.component.SectionHeader
import com.example.minlishlite.presentation.deck.StatBox
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.AccentOrange
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary

@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = viewModel(factory = ProgressViewModel.provideFactory())
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingState(modifier = modifier.fillMaxSize())
        state.errorMessage != null -> ErrorState(
            message = state.errorMessage ?: "Không thể tải tiến độ",
            onRetryClick = {},
            modifier = modifier.fillMaxSize()
        )
        state.analytics != null -> ProgressContent(
            analytics = state.analytics!!,
            modifier = modifier
        )
        else -> EmptyState(
            title = "Chưa có dữ liệu",
            message = "Hãy học vài từ để xem thống kê tiến độ.",
            icon = Icons.Outlined.BarChart,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ProgressContent(
    analytics: ProgressAnalytics,
    modifier: Modifier = Modifier
) {
    val hasReviews = analytics.weeklyActivity.any { it.reviewCount > 0 } ||
        analytics.wordsLearned > 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tiến độ học",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Text(
                text = "${analytics.totalWords} từ trong thư viện",
                fontSize = 14.sp,
                color = OnSurfaceMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    label = "Đã học",
                    value = analytics.wordsLearned.toString(),
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Cần ôn",
                    value = analytics.dueToday.toString(),
                    color = if (analytics.dueToday > 0) AccentOrange else OnSurfaceMuted,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    label = "Độ chính xác",
                    value = "${analytics.accuracyPercent}%",
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Streak",
                    value = "${analytics.streakDays} ngày",
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            // Simple text display of weekly activity instead of custom charts
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Số từ ôn tập trong tuần",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        analytics.weeklyActivity.forEach { activity ->
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                Text(text = activity.label, fontSize = 12.sp, color = OnSurfaceMuted)
                                Text(
                                    text = activity.reviewCount.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activity.reviewCount > 0) AccentGreen else OnSurfaceMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    label = "Tỷ lệ nhớ từ",
                    value = "${analytics.retentionPercent}%",
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Trình độ ước lượng",
                    value = analytics.levelLabel,
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionHeader(title = "Thành tựu gần đây")
        }

        item {
            if (analytics.achievements.isEmpty()) {
                Text(
                    text = "Chưa có thành tựu.",
                    fontSize = 14.sp,
                    color = OnSurfaceMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    analytics.achievements.forEach { achievement ->
                        androidx.compose.material3.Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.CardDefaults.cardColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (achievement.unlocked) AccentGreen.copy(alpha = 0.5f) else OnSurfaceMuted.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (achievement.unlocked) "🏆" else "🔒",
                                    fontSize = 20.sp
                                )
                                Column {
                                    Text(
                                        text = achievement.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (achievement.unlocked) OnSurface else OnSurfaceMuted
                                    )
                                    Text(
                                        text = achievement.description,
                                        fontSize = 12.sp,
                                        color = OnSurfaceMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!hasReviews) {
            item {
                Text(
                    text = "Bắt đầu ôn tập để xem biểu đồ và thành tựu cập nhật.",
                    fontSize = 13.sp,
                    color = OnSurfaceMuted,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    MinLishLiteTheme {
        ProgressContent(
            analytics = ProgressAnalytics(
                totalWords = 50,
                wordsLearned = 32,
                dueToday = 5,
                accuracyPercent = 82,
                streakDays = 3,
                retentionPercent = 75,
                levelLabel = ProgressCalculator.LEVEL_BEGINNER,
                weeklyActivity = listOf(
                    DayActivity("T2", 2),
                    DayActivity("T3", 4),
                    DayActivity("T4", 0),
                    DayActivity("T5", 3),
                    DayActivity("T6", 1),
                    DayActivity("T7", 2),
                    DayActivity("CN", 5)
                ),
                achievements = listOf(
                    Achievement("Bắt đầu hành trình", "Hoàn thành lần ôn đầu", true),
                    Achievement("Tuần đều đặn", "Streak 7 ngày", false)
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}
