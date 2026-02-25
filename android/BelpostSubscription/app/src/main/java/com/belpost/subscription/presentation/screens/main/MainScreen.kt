package com.belpost.subscription.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.belpost.subscription.R
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.presentation.components.PublicationList
import com.belpost.subscription.presentation.components.RememberSnackbarState
import com.belpost.subscription.presentation.components.rememberSnackbarState
import com.belpost.subscription.presentation.viewmodel.MainViewModel
import com.belpost.subscription.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onOpenDetails: (PublicationDto) -> Unit
) {
    // Подписываемся на StateFlow как на compose‑состояние,
    // чтобы экран реагировал на изменения (Loading -> Success/Error)
    val publicationsState by viewModel.publicationsState.collectAsState()
    val snackbarState: RememberSnackbarState = rememberSnackbarState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.openHelp() }) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Помощь подписчику"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState.hostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = publicationsState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    PublicationList(
                        publications = state.data,
                        onItemClick = onOpenDetails
                    )
                }

                is UiState.Error -> {
                    LaunchedEffect(state.message) {
                        snackbarState.showMessage(state.message)
                    }
                    Text(text = "Не удалось загрузить издания")
                }
            }
        }
    }

    // TODO: Help dialog/screens hookup (viewModel.helpStep)
}

