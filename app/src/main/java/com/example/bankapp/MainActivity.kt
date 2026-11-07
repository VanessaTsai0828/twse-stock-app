package com.example.bankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bankapp.ui.screen.StockListScreen
import com.example.bankapp.ui.theme.BankAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity entry point for the app.
 *
 * enableEdgeToEdge() handles WindowCompat.setDecorFitsSystemWindows internally.
 * Keyboard (IME) insets are handled by imePadding() in the Compose layout,
 * which is the correct approach for API 30+ edge-to-edge apps.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BankAppTheme {
                StockListScreen()
            }
        }
    }
}
