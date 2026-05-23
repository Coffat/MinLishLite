package com.example.minlishlite.presentation.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.domain.model.DayActivity
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.SurfaceVariant

@Composable
fun WeeklyActivityChart(
    weeklyActivity: List<DayActivity>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hoạt động 7 ngày",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Text(
                text = "Số lần ôn tập mỗi ngày",
                fontSize = 12.sp,
                color = OnSurfaceMuted,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            val maxCount = weeklyActivity.maxOfOrNull { it.reviewCount }?.coerceAtLeast(1) ?: 1

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val barCount = weeklyActivity.size.coerceAtLeast(1)
                val gap = size.width * 0.04f
                val barWidth = (size.width - gap * (barCount + 1)) / barCount
                val chartHeight = size.height * 0.85f

                weeklyActivity.forEachIndexed { index, day ->
                    val fraction = day.reviewCount.toFloat() / maxCount.toFloat()
                    val barHeight = chartHeight * fraction
                    val left = gap + index * (barWidth + gap)
                    val top = size.height - barHeight

                    drawRoundRect(
                        color = SurfaceVariant,
                        topLeft = Offset(left, 0f),
                        size = Size(barWidth, chartHeight),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    if (day.reviewCount > 0) {
                        drawRoundRect(
                            color = Primary,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyActivity.forEach { day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day.label,
                            fontSize = 11.sp,
                            color = OnSurfaceMuted
                        )
                        Text(
                            text = day.reviewCount.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeeklyActivityChartPreview() {
    MinLishLiteTheme {
        WeeklyActivityChart(
            weeklyActivity = listOf(
                DayActivity("T2", 2),
                DayActivity("T3", 5),
                DayActivity("T4", 0),
                DayActivity("T5", 3),
                DayActivity("T6", 1),
                DayActivity("T7", 4),
                DayActivity("CN", 2)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
