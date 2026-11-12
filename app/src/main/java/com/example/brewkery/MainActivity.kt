package com.example.brewkery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.brewkery.navigation.AppNavigation
import com.example.brewkery.ui.theme.BrewkeryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BrewkeryTheme {
                AppNavigation()
            }
        }
    }
}
