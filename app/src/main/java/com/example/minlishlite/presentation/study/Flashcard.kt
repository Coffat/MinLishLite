package com.example.minlishlite.presentation.study

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.data.local.entity.WordEntity
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Surface

private const val FLIP_DURATION_MS = 300

@Composable
fun Flashcard(
    word: WordEntity,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = FLIP_DURATION_MS, easing = FastOutSlowInEasing),
        label = "flashcardFlip"
    )

    val showFront = rotation <= 90f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFlip
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showFront) {
                FlashcardFront(
                    word = word.word,
                    pronunciation = word.pronunciation,
                    modifier = Modifier.graphicsLayer { rotationY = 0f }
                )
            } else {
                FlashcardBack(
                    meaning = word.meaning,
                    example = word.example,
                    collocation = word.collocation,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Composable
private fun FlashcardFront(
    word: String,
    pronunciation: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = word,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            textAlign = TextAlign.Center
        )
        if (pronunciation.isNotBlank()) {
            Text(
                text = pronunciation,
                fontSize = 16.sp,
                color = OnSurfaceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        Text(
            text = "Chạm để lật thẻ",
            fontSize = 13.sp,
            color = OnSurfaceMuted,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Composable
private fun FlashcardBack(
    meaning: String,
    example: String,
    collocation: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FlashcardBackField(label = "Nghĩa", value = meaning)
        FlashcardBackField(label = "Ví dụ", value = example)
        FlashcardBackField(label = "Cụm từ", value = collocation)
    }
}

@Composable
private fun FlashcardBackField(
    label: String,
    value: String
) {
    if (value.isBlank()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurfaceMuted
        )
        Text(
            text = value,
            fontSize = if (label == "Nghĩa") 20.sp else 15.sp,
            fontWeight = if (label == "Nghĩa") FontWeight.SemiBold else FontWeight.Normal,
            fontStyle = if (label == "Ví dụ") FontStyle.Italic else FontStyle.Normal,
            color = OnSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private val previewWord = WordEntity(
    id = 1,
    deckId = 1,
    word = "abandon",
    pronunciation = "/əˈbændən/",
    meaning = "từ bỏ, bỏ rơi",
    description = "",
    example = "He abandoned his car in the snow.",
    collocation = "abandon hope",
    relatedWords = "",
    note = "",
    level = "B2",
    nextReviewAt = 0L,
    createdAt = 0L,
    updatedAt = 0L
)

@Preview(showBackground = true, name = "Flashcard Front")
@Composable
private fun FlashcardFrontPreview() {
    MinLishLiteTheme {
        Flashcard(
            word = previewWord,
            isFlipped = false,
            onFlip = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Flashcard Back")
@Composable
private fun FlashcardBackPreview() {
    MinLishLiteTheme {
        Flashcard(
            word = previewWord,
            isFlipped = true,
            onFlip = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
