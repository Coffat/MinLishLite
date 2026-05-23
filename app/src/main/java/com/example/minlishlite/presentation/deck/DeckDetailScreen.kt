package com.example.minlishlite.presentation.deck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.domain.model.Word
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.presentation.component.AppTextField
import com.example.minlishlite.presentation.component.EmptyState
import com.example.minlishlite.presentation.component.ErrorState
import com.example.minlishlite.presentation.component.LoadingState
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.AccentOrange
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.ErrorColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.PrimarySoft
import com.example.minlishlite.ui.theme.Surface
import com.example.minlishlite.ui.theme.SurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    deckId: Int,
    onNavigateToEditDeck: (Int) -> Unit,
    onNavigateToAddWord: (Int) -> Unit,
    onNavigateToStudy: (Int) -> Unit,
    onNavigateToWordDetail: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckDetailViewModel = viewModel(factory = DeckDetailViewModel.provideFactory(deckId))
) {
    val state by viewModel.uiState.collectAsState()
    var wordToDelete by remember { mutableStateOf<Word?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddWord(deckId) },
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm từ mới",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // App Bar / Navigation
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
                    text = "Chi tiết bộ từ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )

                IconButton(
                    onClick = { onNavigateToEditDeck(deckId) },
                    enabled = state.deck != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa bộ từ",
                        tint = Primary
                    )
                }
            }

            when {
                state.isLoading && state.deck == null -> {
                    LoadingState(modifier = Modifier.weight(1f))
                }
                state.error != null && state.deck == null -> {
                    ErrorState(
                        message = state.error ?: "Đã xảy ra lỗi.",
                        onRetryClick = { /* Re-load handled by init or system */ },
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> {
                    val deck = state.deck
                    if (deck != null) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Deck Info Section
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = deck.name,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (deck.tag.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .background(PrimarySoft, RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = deck.tag,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Primary
                                                )
                                            }
                                        }
                                    }

                                    if (deck.description.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = deck.description,
                                            fontSize = 14.sp,
                                            color = OnSurfaceMuted,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }

                            // Stats Cards Row
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatBox(
                                        label = "Tổng số từ",
                                        value = state.totalWordsCount.toString(),
                                        color = OnSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatBox(
                                        label = "Cần ôn",
                                        value = state.dueWordsCount.toString(),
                                        color = AccentOrange,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatBox(
                                        label = "Đã học",
                                        value = state.learnedWordsCount.toString(),
                                        color = AccentGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Study Action Button
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                AppButton(
                                    text = "Học ngay (${state.dueWordsCount} từ cần ôn)",
                                    onClick = { onNavigateToStudy(deckId) },
                                    icon = Icons.Default.PlayArrow,
                                    enabled = state.totalWordsCount > 0,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Divider
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(BorderColor)
                                )
                            }

                            // Search and Filter Header
                            item {
                                Column {
                                    Text(
                                        text = "Danh sách từ vựng",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    AppTextField(
                                        value = state.searchQuery,
                                        onValueChange = { viewModel.onSearchQueryChange(it) },
                                        placeholder = "Tìm kiếm từ hoặc nghĩa...",
                                        leadingIcon = Icons.Default.Search,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Filter Chips
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChipItem(
                                            text = "Tất cả",
                                            isSelected = state.selectedFilter == WordFilter.ALL,
                                            onClick = { viewModel.onFilterSelect(WordFilter.ALL) }
                                        )
                                        FilterChipItem(
                                            text = "Cần ôn",
                                            isSelected = state.selectedFilter == WordFilter.DUE_TODAY,
                                            onClick = { viewModel.onFilterSelect(WordFilter.DUE_TODAY) }
                                        )
                                        FilterChipItem(
                                            text = "Đã học",
                                            isSelected = state.selectedFilter == WordFilter.LEARNED,
                                            onClick = { viewModel.onFilterSelect(WordFilter.LEARNED) }
                                        )
                                    }
                                }
                            }

                            // Word List
                            if (state.words.isEmpty()) {
                                item {
                                    val message = if (state.searchQuery.isNotEmpty()) {
                                        "Không tìm thấy từ vựng nào khớp với tìm kiếm."
                                    } else if (state.selectedFilter != WordFilter.ALL) {
                                        "Không có từ vựng nào thuộc bộ lọc này."
                                    } else {
                                        "Chưa có từ vựng nào trong bộ từ này. Hãy nhấn nút + ở góc dưới để thêm từ mới đầu tiên!"
                                    }
                                    EmptyState(
                                        title = "Không có từ vựng",
                                        message = message,
                                        icon = Icons.Outlined.Book
                                    )
                                }
                            } else {
                                items(state.words, key = { it.id }) { word ->
                                    WordItem(
                                        word = word,
                                        onClick = { onNavigateToWordDetail(word.id) },
                                        onDeleteClick = { wordToDelete = word }
                                    )
                                }
                            }

                            // Extra bottom padding
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    wordToDelete?.let { word ->
        AlertDialog(
            onDismissRequest = { wordToDelete = null },
            confirmButton = {
                AppButton(
                    text = "Xóa từ",
                    onClick = {
                        viewModel.onDeleteWord(word.id)
                        wordToDelete = null
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            dismissButton = {
                AppOutlinedButton(
                    text = "Hủy",
                    onClick = { wordToDelete = null },
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
                    text = "Bạn có chắc chắn muốn xóa từ '${word.word}' khỏi bộ từ này không?",
                    fontSize = 14.sp,
                    color = OnSurfaceMuted
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = OnSurfaceMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) Primary else Surface
    val contentColor = if (isSelected) Color.White else OnSurfaceMuted
    val borderColor = if (isSelected) Primary else BorderColor

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun WordItem(
    word: Word,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicator Dot
            val dotColor = when {
                word.nextReviewAt <= System.currentTimeMillis() -> AccentOrange // Due Today
                word.reviewCount > 0 -> AccentGreen // Learned
                else -> OnSurfaceMuted // New (Not learned)
            }
            
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.word,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    
                    if (word.pronunciation.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = word.pronunciation,
                            fontSize = 12.sp,
                            color = OnSurfaceMuted,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = word.meaning,
                    fontSize = 13.sp,
                    color = OnSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .background(Color(0xFFFEF2F2), RoundedCornerShape(10.dp))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa từ",
                    tint = ErrorColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeckDetailScreenPreview() {
    MinLishLiteTheme {
        DeckDetailScreen(
            deckId = 1,
            onNavigateToEditDeck = {},
            onNavigateToAddWord = {},
            onNavigateToStudy = {},
            onNavigateToWordDetail = {},
            onBackClick = {}
        )
    }
}
