package com.example.minlishlite.presentation.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.domain.model.Deck
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.ui.theme.AccentGreen
import com.example.minlishlite.ui.theme.AccentOrange
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted
import com.example.minlishlite.ui.theme.Primary
import com.example.minlishlite.ui.theme.PrimarySoft

@Composable
fun DeckSummarySection(
    deck: Deck,
    totalWordsCount: Int,
    dueWordsCount: Int,
    learnedWordsCount: Int,
    onStudyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatBox(
                label = "Tổng số từ",
                value = totalWordsCount.toString(),
                color = OnSurface,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Cần ôn",
                value = dueWordsCount.toString(),
                color = AccentOrange,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Đã học",
                value = learnedWordsCount.toString(),
                color = AccentGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        AppButton(
            text = "Học ngay ($dueWordsCount từ cần ôn)",
            onClick = onStudyClick,
            icon = Icons.Default.PlayArrow,
            enabled = totalWordsCount > 0,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
