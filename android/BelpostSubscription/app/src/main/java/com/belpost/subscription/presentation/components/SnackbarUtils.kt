package com.belpost.subscription.presentation.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class RememberSnackbarState(
    val hostState: SnackbarHostState
) {
    suspend fun showMessage(message: String) {
        hostState.showSnackbar(message)
    }
}

@Composable
fun rememberSnackbarState(): RememberSnackbarState {
    val hostState = remember { SnackbarHostState() }
    return RememberSnackbarState(hostState)
}

