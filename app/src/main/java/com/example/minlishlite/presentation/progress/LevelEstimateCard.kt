package com.example.minlishlite.presentation.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.core.util.ProgressCalculator
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary

@Composable
fun LevelEstimateCard(
    levelLabel: String,
    wordsLearned: Int,
    modifier: Modifier = Modifier
) {
    val hint = when (levelLabel) {
        ProgressCalculator.LEVEL_BEGINNER -> "Dưới 300 từ đã học"
        ProgressCalculator.LEVEL_INTERMEDIATE -> "300–1000 từ đã học"
        else -> "Trên 1000 từ đã học"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trình độ ước tính",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceMuted
            )
            Text(
                text = levelLabel,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "$wordsLearned từ · $hint",
                fontSize = 12.sp,
                color = OnSurfaceMuted,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LevelEstimateCardPreview() {
    MinLishLiteTheme {
        LevelEstimateCard(
            levelLabel = ProgressCalculator.LEVEL_INTERMEDIATE,
            wordsLearned = 420,
            modifier = Modifier.padding(16.dp)
        )
    }
}
