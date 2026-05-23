package com.example.minlishlite.presentation.deck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.presentation.component.AppButton
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface

@Composable
fun DeckDetailScreen(
    deckId: Int,
    onNavigateToEditDeck: (Int) -> Unit,
    onNavigateToAddWord: (Int) -> Unit,
    onNavigateToStudy: (Int) -> Unit,
    onNavigateToWordDetail: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Deck Detail Screen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Route: deck_detail/$deckId",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Passed Parameter deckId = $deckId",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = "Study Deck",
            onClick = { onNavigateToStudy(deckId) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppOutlinedButton(
            text = "Edit Deck Name/Description",
            onClick = { onNavigateToEditDeck(deckId) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppOutlinedButton(
            text = "Add Word to Deck",
            onClick = { onNavigateToAddWord(deckId) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppOutlinedButton(
            text = "View Word 1 Details",
            onClick = { onNavigateToWordDetail(1) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppOutlinedButton(
            text = "Back",
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        )
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
