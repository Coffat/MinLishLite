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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minlishlite.core.util.PronunciationAudioPlayer
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.presentation.component.AppTextField
import com.example.minlishlite.presentation.deck.FilterChipItem
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.ErrorColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWordScreen(
    deckId: Int?,
    wordId: Int?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditWordViewModel = viewModel(
        factory = AddEditWordViewModel.provideFactory(deckId, wordId)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isEdit = wordId != null
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose { PronunciationAudioPlayer.stop() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(16.dp)
            ) {
                AppButton(
                    text = if (state.isSaving) "Đang lưu..." else "Lưu từ vựng",
                    onClick = {
                        viewModel.onSaveWord(onSuccess = onBackClick)
                    },
                    icon = Icons.Default.Save,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
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
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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
                    text = if (isEdit) "Sửa từ vựng" else "Thêm từ mới",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card 1: Word details spelling & lookup
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
                                text = "Thông tin từ vựng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Word spelling input
                            AppTextField(
                                value = state.word,
                                onValueChange = { viewModel.onWordChange(it) },
                                label = "Từ tiếng Anh *",
                                placeholder = "Ví dụ: hello",
                                errorText = state.wordError,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Lookup Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AppOutlinedButton(
                                    text = if (state.isSearching) "Đang tìm..." else "Tra nghĩa",
                                    onClick = { viewModel.lookupWordInDictionary() },
                                    icon = Icons.Default.Search,
                                    enabled = state.word.isNotBlank() && !state.isSearching,
                                    modifier = Modifier.weight(1f)
                                )

                                if (state.isSearching) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = Primary
                                    )
                                }
                            }

                            // Search feedback messages
                            if (state.searchSuccessMsg != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.searchSuccessMsg ?: "",
                                    color = AccentGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (state.searchError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.searchError ?: "",
                                    color = ErrorColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            state.lookupPreview?.let { preview ->
                                Spacer(modifier = Modifier.height(12.dp))
                                DictionaryLookupPreviewCard(
                                    preview = preview,
                                    onApplyClick = { viewModel.applyLookupPreview() },
                                    onDismissClick = { viewModel.dismissLookupPreview() },
                                    onPlayUkAudio = {
                                        PronunciationAudioPlayer.play(context, preview.pronunciationUkAudioUrl)
                                    },
                                    onPlayUsAudio = {
                                        PronunciationAudioPlayer.play(context, preview.pronunciationUsAudioUrl)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Phát âm / Phiên âm",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            val ukAudioUrl = state.lookupPreview?.pronunciationUkAudioUrl
                                ?.takeIf { it.isNotBlank() }
                                ?: state.pronunciationUkAudioUrl.takeIf { it.isNotBlank() }

                            PronunciationField(
                                label = "Anh-Anh (UK)",
                                value = state.pronunciationUk,
                                onValueChange = { viewModel.onPronunciationUkChange(it) },
                                audioUrl = ukAudioUrl,
                                onPlayAudio = { PronunciationAudioPlayer.play(context, ukAudioUrl.orEmpty()) },
                                placeholder = "Ví dụ: /həˈləʊ/"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val usAudioUrl = state.lookupPreview?.pronunciationUsAudioUrl
                                ?.takeIf { it.isNotBlank() }
                                ?: state.pronunciationUsAudioUrl.takeIf { it.isNotBlank() }

                            PronunciationField(
                                label = "Anh-Mỹ (US)",
                                value = state.pronunciationUs,
                                onValueChange = { viewModel.onPronunciationUsChange(it) },
                                audioUrl = usAudioUrl,
                                onPlayAudio = { PronunciationAudioPlayer.play(context, usAudioUrl.orEmpty()) },
                                placeholder = "Ví dụ: /həˈloʊ/"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Level selection
                            Text(
                                text = "Mức độ / Trình độ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChipItem(
                                    text = "Beginner",
                                    isSelected = state.level == "Beginner",
                                    onClick = { viewModel.onLevelChange("Beginner") }
                                )
                                FilterChipItem(
                                    text = "Intermediate",
                                    isSelected = state.level == "Intermediate",
                                    onClick = { viewModel.onLevelChange("Intermediate") }
                                )
                                FilterChipItem(
                                    text = "Advanced",
                                    isSelected = state.level == "Advanced",
                                    onClick = { viewModel.onLevelChange("Advanced") }
                                )
                            }
                        }
                    }
                }

                // Card 2: Translation & Meaning
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
                                text = "Nghĩa & Định nghĩa",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Meaning input
                            AppTextField(
                                value = state.meaning,
                                onValueChange = { viewModel.onMeaningChange(it) },
                                label = "Nghĩa tiếng Việt *",
                                placeholder = "Ví dụ: Xin chào",
                                errorText = state.meaningError,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description input (Multi-line)
                            AppTextField(
                                value = state.description,
                                onValueChange = { viewModel.onDescriptionChange(it) },
                                label = "Định nghĩa / Giải thích thêm",
                                placeholder = "Giải nghĩa chi tiết hoặc từ loại của từ...",
                                singleLine = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Card 3: Usage examples & Notes
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
                                text = "Cách dùng & Ghi chú",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Example (Multi-line)
                            AppTextField(
                                value = state.example,
                                onValueChange = { viewModel.onExampleChange(it) },
                                label = "Ví dụ đặt câu",
                                placeholder = "Ví dụ: Hello, nice to meet you!",
                                singleLine = false,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Collocation
                            AppTextField(
                                value = state.collocation,
                                onValueChange = { viewModel.onCollocationChange(it) },
                                label = "Cụm từ thường đi kèm (Collocation)",
                                placeholder = "Ví dụ: say hello to, hello there",
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Related words
                            AppTextField(
                                value = state.relatedWords,
                                onValueChange = { viewModel.onRelatedWordsChange(it) },
                                label = "Từ liên quan (Đồng nghĩa / Trái nghĩa)",
                                placeholder = "Ví dụ: hi, hey, greetings",
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Note (Multi-line)
                            AppTextField(
                                value = state.note,
                                onValueChange = { viewModel.onNoteChange(it) },
                                label = "Ghi chú cá nhân",
                                placeholder = "Mẹo ghi nhớ, quy tắc phát âm...",
                                singleLine = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Spacing at the bottom so it doesn't get covered by the bottom bar
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun PronunciationField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    audioUrl: String?,
    onPlayAudio: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        trailingIcon = if (audioUrl.isNullOrBlank()) null else Icons.Default.VolumeUp,
        onTrailingIconClick = if (audioUrl.isNullOrBlank()) null else onPlayAudio,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun DictionaryLookupPreviewCard(
    preview: DictionaryLookupPreview,
    onApplyClick: () -> Unit,
    onDismissClick: () -> Unit,
    onPlayUkAudio: () -> Unit,
    onPlayUsAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.25f)),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Kết quả tra cứu",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            if (preview.meaning.isNotBlank()) {
                PreviewHighlightBlock(
                    label = "Nghĩa tiếng Việt",
                    value = preview.meaning
                )
            }

            if (preview.pronunciationUk.isNotBlank() || preview.pronunciationUs.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Phát âm",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceMuted
                    )

                    if (preview.pronunciationUk.isNotBlank()) {
                        PreviewPronunciationRow(
                            regionLabel = "Anh-Anh",
                            phonetic = preview.pronunciationUk,
                            hasAudio = preview.pronunciationUkAudioUrl.isNotBlank(),
                            onPlayAudio = onPlayUkAudio
                        )
                    }

                    if (preview.pronunciationUs.isNotBlank()) {
                        PreviewPronunciationRow(
                            regionLabel = "Anh-Mỹ",
                            phonetic = preview.pronunciationUs,
                            hasAudio = preview.pronunciationUsAudioUrl.isNotBlank(),
                            onPlayAudio = onPlayUsAudio
                        )
                    }
                }
            }

            if (preview.description.isNotBlank()) {
                PreviewHighlightBlock(
                    label = "Định nghĩa",
                    value = preview.description
                )
            }

            if (preview.example.isNotBlank()) {
                PreviewHighlightBlock(
                    label = "Ví dụ",
                    value = preview.example
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppOutlinedButton(
                    text = "Bỏ qua",
                    onClick = onDismissClick,
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = "Áp dụng",
                    onClick = onApplyClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PreviewHighlightBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Background, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurfaceMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = OnSurface,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun PreviewPronunciationRow(
    regionLabel: String,
    phonetic: String,
    hasAudio: Boolean,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Background, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = regionLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceMuted
            )
            Text(
                text = phonetic,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
        }

        if (hasAudio) {
            IconButton(onClick = onPlayAudio) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Nghe phát âm $regionLabel",
                    tint = Primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditWordScreenPreview() {
    MinLishLiteTheme {
        AddEditWordScreen(
            deckId = 1,
            wordId = null,
            onBackClick = {}
        )
    }
}
