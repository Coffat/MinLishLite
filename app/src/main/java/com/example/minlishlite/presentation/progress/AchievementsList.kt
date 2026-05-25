package com.example.minlishlite.presentation.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.data.model.Achievement
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.BorderColor
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.PrimarySoft
import com.example.minlishlite.ui.theme.SurfaceVariant

@Composable
fun AchievementsList(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        achievements.forEach { achievement ->
            AchievementItem(achievement = achievement)
        }
    }
}

@Composable
private fun AchievementItem(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val alpha = if (achievement.unlocked) 1f else 0.55f
    val iconTint = if (achievement.unlocked) AccentGreen else OnSurfaceMuted
    val containerColor = if (achievement.unlocked) PrimarySoft else SurfaceVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = containerColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (achievement.unlocked) {
                        Icons.Outlined.EmojiEvents
                    } else {
                        Icons.Outlined.Lock
                    },
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = OnSurfaceMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (achievement.unlocked) {
                Text(
                    text = "✓",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AchievementsListPreview() {
    MinLishLiteTheme {
        AchievementsList(
            achievements = listOf(
                Achievement("Bắt đầu hành trình", "Hoàn thành lần ôn đầu", true),
                Achievement("Tuần đều đặn", "Streak 7 ngày", false)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
