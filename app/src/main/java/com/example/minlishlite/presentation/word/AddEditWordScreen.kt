package com.example.minlishlite.presentation.word

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
fun AddEditWordScreen(
    deckId: Int?,
    wordId: Int?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEdit = wordId != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isEdit) "Edit Word Screen" else "Add Word Screen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isEdit) "Route: edit_word/$wordId" else "Route: add_word/$deckId",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEdit) {
            Text(
                text = "Passed Parameter wordId = $wordId",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
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
fun AddEditWordScreenPreview() {
    MinLishLiteTheme {
        AddEditWordScreen(
            deckId = 1,
            wordId = null,
            onBackClick = {}
        )
    }
}
