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
import com.example.minlishlite.presentation.component.AppOutlinedButton
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface

@Composable
fun AddEditDeckScreen(
    deckId: Int?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEdit = deckId != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isEdit) "Edit Deck Screen" else "Add Deck Screen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isEdit) "Route: edit_deck/$deckId" else "Route: add_deck",
            fontSize = 14.sp
        )

        if (isEdit) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Passed Parameter deckId = $deckId",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AppOutlinedButton(
            text = "Back",
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditDeckScreenPreview() {
    MinLishLiteTheme {
        AddEditDeckScreen(
            deckId = 42,
            onBackClick = {}
        )
    }
}
