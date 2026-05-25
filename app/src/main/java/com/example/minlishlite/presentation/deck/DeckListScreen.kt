package com.example.minlishlite.presentation.deck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.data.local.entity.DeckEntity
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.presentation.component.AppTextField
import com.example.minlishlite.presentation.component.EmptyState
import com.example.minlishlite.presentation.component.LoadingState
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.ErrorColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.PrimarySoft
import com.example.minlishlite.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    onNavigateToAddDeck: () -> Unit,
    onNavigateToDeckDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckListViewModel = viewModel(factory = DeckListViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var deckToDelete by remember { mutableStateOf<DeckEntity?>(null) }
    val tagScrollState = rememberScrollState()
    val emptyMessage by remember(state.searchQuery, state.selectedTag) {
        derivedStateOf {
            if (state.searchQuery.isNotEmpty() || state.selectedTag != null) {
                "Không tìm thấy bộ từ vựng nào phù hợp với tìm kiếm."
            } else {
                "Bạn chưa tạo bộ từ vựng nào. Hãy nhấn nút + ở góc màn hình để tạo mới!"
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bộ từ vựng",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            AppTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = "Tìm kiếm bộ từ...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tag Filter Chips (Scrollable Row)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(tagScrollState)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" Chip
                TagChip(
                    text = "Tất cả",
                    isSelected = state.selectedTag == null,
                    onClick = { viewModel.onTagSelect(null) }
                )

                // Dynamic Tags Chips
                state.allTags.forEach { tag ->
                    TagChip(
                        text = tag,
                        isSelected = state.selectedTag == tag,
                        onClick = { viewModel.onTagSelect(tag) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DeckEntity List or Empty State
            when {
                state.isLoading -> {
                    LoadingState(modifier = Modifier.weight(1f))
                }
                state.decks.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f)) {
                        EmptyState(
                            title = "Danh sách trống",
                            message = emptyMessage,
                            icon = Icons.Outlined.Inbox
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.decks, key = { it.id }) { deck ->
                            DeckCardItem(
                                deck = deck,
                                onClick = { onNavigateToDeckDetail(deck.id) },
                                onDeleteClick = { deckToDelete = deck }
                            )
                        }
                        // Spacer at the bottom so elements don't get covered by FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNavigateToAddDeck,
            containerColor = Primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Thêm bộ từ mới",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Delete Confirmation Dialog
    deckToDelete?.let { deck ->
        AlertDialog(
            onDismissRequest = { deckToDelete = null },
            confirmButton = {
                AppButton(
                    text = "Xóa bộ từ",
                    onClick = {
                        viewModel.onDeleteDeck(deck.id)
                        deckToDelete = null
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            dismissButton = {
                AppOutlinedButton(
                    text = "Hủy",
                    onClick = { deckToDelete = null },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            },
            title = {
                Text(
                    text = "Xóa bộ từ vựng?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn xóa bộ từ '${deck.name}' không? Hành động này cũng sẽ xóa toàn bộ từ vựng và lịch sử học liên quan.",
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
fun TagChip(
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
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun DeckCardItem(
    deck: DeckEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = deck.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (deck.tag.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(PrimarySoft, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = deck.tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }

                if (deck.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deck.description,
                        fontSize = 13.sp,
                        color = OnSurfaceMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = ErrorColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeckListScreenPreview() {
    MinLishLiteTheme {
        DeckListScreen(
            onNavigateToAddDeck = {},
            onNavigateToDeckDetail = {}
        )
    }
}
