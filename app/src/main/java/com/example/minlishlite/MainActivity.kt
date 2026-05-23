package com.example.minlishlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.minlishlite.presentation.navigation.AppNavigation
import com.example.minlishlite.ui.theme.MinLishLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinLishLiteTheme {
                AppNavigation()
            }
        }
    }
}