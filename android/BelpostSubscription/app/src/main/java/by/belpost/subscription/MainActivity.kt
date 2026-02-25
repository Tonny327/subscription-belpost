package com.belpost.subscription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belpost.subscription.presentation.navigation.BelpostNavHost
import com.belpost.subscription.presentation.theme.BelpostTheme

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer is not provided")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as BelpostApplication).appContainer

        setContent {
            CompositionLocalProvider(LocalAppContainer provides appContainer) {
                BelpostApp()
            }
        }
    }
}

@Composable
fun BelpostApp() {
    BelpostTheme {
        Surface {
            BelpostNavHost()
        }
    }
}