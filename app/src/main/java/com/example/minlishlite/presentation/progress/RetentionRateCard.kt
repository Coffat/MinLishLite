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
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary

@Composable
fun RetentionRateCard(
    retentionPercent: Int,
    modifier: Modifier = Modifier
) {
    val valueColor = if (retentionPercent >= 70) AccentGreen else Primary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Retention",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceMuted
            )
            Text(
                text = "$retentionPercent%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Tỷ lệ chọn Good / Easy trên tổng lần ôn",
                fontSize = 12.sp,
                color = OnSurfaceMuted,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RetentionRateCardPreview() {
    MinLishLiteTheme {
        RetentionRateCard(
            retentionPercent = 75,
            modifier = Modifier.padding(16.dp)
        )
    }
}
