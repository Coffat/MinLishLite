package com.example.minlishlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.minlishlite.presentation.component.ComponentShowcaseScreen
import com.example.minlishlite.ui.theme.MinLishLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinLishLiteTheme {
                ComponentShowcaseScreen()
            }
        }
    }
}
