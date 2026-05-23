package com.example.minlishlite.presentation.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.domain.model.ReviewResult
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.AccentOrange
import com.example.minlishlite.ui.theme.AccentYellow
import com.example.minlishlite.ui.theme.ErrorColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme

@Composable
fun ReviewRatingButtons(
    onRate: (ReviewResult) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RatingButton(
                text = "Again",
                containerColor = ErrorColor,
                onClick = { onRate(ReviewResult.AGAIN) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            RatingButton(
                text = "Hard",
                containerColor = AccentOrange,
                onClick = { onRate(ReviewResult.HARD) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RatingButton(
                text = "Good",
                containerColor = AccentYellow,
                onClick = { onRate(ReviewResult.GOOD) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            RatingButton(
                text = "Easy",
                containerColor = AccentGreen,
                onClick = { onRate(ReviewResult.EASY) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RatingButton(
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewRatingButtonsPreview() {
    MinLishLiteTheme {
        ReviewRatingButtons(
            onRate = {},
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
