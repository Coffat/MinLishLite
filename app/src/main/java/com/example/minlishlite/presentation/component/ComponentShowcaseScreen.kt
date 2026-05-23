package com.example.minlishlite.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minlishlite.ui.theme.Background
import com.example.minlishlite.ui.theme.MinLishLiteTheme
import com.example.minlishlite.ui.theme.OnSurface
import com.example.minlishlite.ui.theme.OnSurfaceMuted

@Composable
fun ComponentShowcaseScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Showcase", "Empty State", "Loading State", "Error State")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontSize = 12.sp) }
                )
            }
        }

        when (selectedTab) {
            0 -> ShowcaseSection()
            1 -> EmptyState(
                title = "No Decks Available",
                message = "Create your first vocabulary deck to start learning with flashcards and spaced repetition.",
                icon = Icons.Outlined.Inbox,
                actionText = "Create Deck",
                onActionClick = { selectedTab = 0 }
            )
            2 -> LoadingState()
            3 -> ErrorState(
                message = "Unable to connect to the dictionary server. Please check your internet connection and try again.",
                onRetryClick = { selectedTab = 0 }
            )
        }
    }
}

@Composable
private fun ShowcaseSection() {
    var textValue by remember { mutableStateOf("") }
    var errorTextValue by remember { mutableStateOf("wrong_format") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "MinLish Components",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(color = Color(0xFFE2E8F0))

        SectionHeader(
            title = "Section Headers",
            actionText = "See all",
            onActionClick = {}
        )

        HorizontalDivider(color = Color(0xFFE2E8F0))

        Text(
            text = "Buttons",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceMuted
        )

        AppButton(
            text = "Primary Button",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        AppOutlinedButton(
            text = "Secondary Outlined Button",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        AppDestructiveButton(
            text = "Destructive Button",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.Start) {
            AppTextButton(
                text = "Text Button",
                onClick = {}
            )
            Spacer(modifier = Modifier.width(16.dp))
            AppTextButton(
                text = "Text Button with Icon",
                icon = Icons.Default.Info,
                onClick = {}
            )
        }

        HorizontalDivider(color = Color(0xFFE2E8F0))

        Text(
            text = "Text Inputs",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceMuted
        )

        AppTextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = "Enter Word *",
            placeholder = "e.g., Ephemeral",
            leadingIcon = Icons.Default.Info
        )

        AppTextField(
            value = errorTextValue,
            onValueChange = { errorTextValue = it },
            label = "Email Address",
            errorText = "Invalid email format"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ComponentShowcaseScreenPreview() {
    MinLishLiteTheme {
        ComponentShowcaseScreen()
    }
}
