package com.example.minlishlite.presentation.word

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppDestructiveButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.presentation.component.ErrorState
import com.example.minlishlite.presentation.component.LoadingState
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.AccentOrange
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.PrimarySoft
import com.example.minlishlite.ui.theme.ErrorColor
import com.example.minlishlite.ui.theme.Secondary
import com.example.minlishlite.ui.theme.Surface
import com.example.minlishlite.ui.theme.SurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    wordId: Int,
    onNavigateToEditWord: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WordDetailViewModel = viewModel(factory = WordDetailViewModel.provideFactory(wordId))
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Reload word details when entering the screen (helpful if edited)
    LaunchedEffect(wordId) {
        viewModel.loadWordDetails()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = OnSurface
                    )
                }

                Text(
                    text = "Chi tiết từ vựng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )

                IconButton(
                    onClick = { onNavigateToEditWord(wordId) },
                    enabled = state.word != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa từ vựng",
                        tint = Primary
                    )
                }
            }

            when {
                state.isLoading -> {
                    LoadingState(modifier = Modifier.weight(1f))
                }
                state.error != null -> {
                    ErrorState(
                        message = state.error ?: "Đã xảy ra lỗi.",
                        onRetryClick = { viewModel.loadWordDetails() },
                        modifier = Modifier.weight(1f)
                    )
                }
                state.word != null -> {
                    val word = state.word!!
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Word title & pronunciation card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BorderColor),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = word.word,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .background(PrimarySoft, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = word.level,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Primary
                                            )
                                        }
                                    }

                                    if (word.pronunciation.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = word.pronunciation,
                                            fontSize = 16.sp,
                                            color = OnSurfaceMuted,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }

                        // Meaning & definition card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BorderColor),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Nghĩa tiếng Việt",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Secondary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = word.meaning,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )

                                    if (word.description.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Giải thích chi tiết",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Secondary,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = word.description,
                                            fontSize = 14.sp,
                                            color = OnSurface,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Examples and usage card (if any values exist)
                        if (word.example.isNotEmpty() || word.collocation.isNotEmpty() || word.relatedWords.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, BorderColor),
                                    colors = CardDefaults.cardColors(containerColor = Surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        if (word.example.isNotEmpty()) {
                                            Column {
                                                Text(
                                                    text = "Ví dụ đặt câu",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Secondary,
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                                Text(
                                                    text = word.example,
                                                    fontSize = 14.sp,
                                                    color = OnSurface,
                                                    fontStyle = FontStyle.Italic,
                                                    lineHeight = 20.sp
                                                )
                                            }
                                        }

                                        if (word.collocation.isNotEmpty()) {
                                            Column {
                                                Text(
                                                    text = "Cụm từ thường dùng (Collocation)",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Secondary,
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                                Text(
                                                    text = word.collocation,
                                                    fontSize = 14.sp,
                                                    color = OnSurface
                                                )
                                            }
                                        }

                                        if (word.relatedWords.isNotEmpty()) {
                                            Column {
                                                Text(
                                                    text = "Từ liên quan",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Secondary,
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                                Text(
                                                    text = word.relatedWords,
                                                    fontSize = 14.sp,
                                                    color = OnSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Personal notes card (if value exists)
                        if (word.note.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFEF3C7)), // Soft yellow border
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)), // Soft yellow background
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Ghi chú cá nhân",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309), // Amber/Orange accent
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = word.note,
                                            fontSize = 14.sp,
                                            color = OnSurface,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Learning Progress & SRS stats card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BorderColor),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Tiến độ học tập",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    // Status info
                                    val isDue = word.nextReviewAt <= System.currentTimeMillis()
                                    val statusText = when {
                                        isDue -> "Cần ôn tập hôm nay"
                                        word.reviewCount > 0 -> "Đã học"
                                        else -> "Từ mới chưa học"
                                    }
                                    val statusColor = when {
                                        isDue -> AccentOrange
                                        word.reviewCount > 0 -> AccentGreen
                                        else -> Secondary
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Trạng thái",
                                            fontSize = 14.sp,
                                            color = OnSurfaceMuted
                                        )
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(statusColor, RoundedCornerShape(999.dp))
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = statusText,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Next review date
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Lần ôn tập tiếp theo",
                                            fontSize = 14.sp,
                                            color = OnSurfaceMuted
                                        )
                                        Text(
                                            text = formatDate(word.nextReviewAt),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = OnSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Review counts
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Số lần ôn tập",
                                            fontSize = 14.sp,
                                            color = OnSurfaceMuted
                                        )
                                        Text(
                                            text = "${word.reviewCount} lần",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = OnSurface
                                        )
                                    }

                                    if (word.reviewCount > 0) {
                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Accuracy rate
                                        val accuracyPercent = if (word.reviewCount > 0) {
                                            (word.correctCount.toFloat() / word.reviewCount * 100).toInt()
                                        } else 0
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Tỷ lệ nhớ đúng",
                                                fontSize = 14.sp,
                                                color = OnSurfaceMuted
                                            )
                                            Text(
                                                text = "$accuracyPercent%",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (accuracyPercent >= 80) AccentGreen else if (accuracyPercent >= 50) OnSurface else ErrorColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Delete word button
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            AppDestructiveButton(
                                text = "Xóa từ vựng này",
                                onClick = { showDeleteConfirm = true },
                                icon = Icons.Default.Delete,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            confirmButton = {
                AppButton(
                    text = "Xóa từ",
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.onDeleteWord(onSuccess = onBackClick)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            dismissButton = {
                AppOutlinedButton(
                    text = "Hủy",
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            },
            title = {
                Text(
                    text = "Xóa từ vựng?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            },
            text = {
                Text(
                    text = "Hành động này sẽ xóa hoàn toàn từ vựng này khỏi hệ thống và không thể phục hồi.",
                    fontSize = 14.sp,
                    color = OnSurfaceMuted
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(date)
}

@Preview(showBackground = true)
@Composable
fun WordDetailScreenPreview() {
    MinLishLiteTheme {
        WordDetailScreen(
            wordId = 1,
            onNavigateToEditWord = {},
            onBackClick = {}
        )
    }
}
