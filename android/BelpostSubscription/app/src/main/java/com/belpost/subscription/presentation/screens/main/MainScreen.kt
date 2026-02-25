package com.belpost.subscription.presentation.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.belpost.subscription.R
import com.belpost.subscription.data.api.models.CategoryDto
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.presentation.components.PublicationList
import com.belpost.subscription.presentation.components.RememberSnackbarState
import com.belpost.subscription.presentation.components.rememberSnackbarState
import com.belpost.subscription.presentation.viewmodel.HelpStep
import com.belpost.subscription.presentation.viewmodel.MainViewModel
import com.belpost.subscription.utils.UiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onOpenDetails: (PublicationDto) -> Unit,
    cartCount: Int,
    onOpenCart: () -> Unit,
    onOpenProfile: () -> Unit,
    onAddToCart: (PublicationDto) -> Unit
) {
    val publicationsState by viewModel.publicationsState.collectAsState()
    val helpStep by viewModel.helpStep.collectAsState()
    val snackbarState: RememberSnackbarState = rememberSnackbarState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Личный кабинет"
                        )
                    }
                    IconButton(onClick = onOpenCart) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge {
                                        Text(text = cartCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Корзина"
                            )
                        }
                    }
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
        val isRefreshing = publicationsState is UiState.Loading
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
            onRefresh = { viewModel.refreshPublications() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = publicationsState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is UiState.Success -> {
                        PublicationList(
                            publications = state.data,
                            onItemClick = onOpenDetails,
                            onAddToCart = onAddToCart
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
    }

    if (helpStep != null) {
        HelpWizardDialog(
            step = helpStep!!,
            onDismiss = { viewModel.dismissHelp() },
            onSelectType = { type -> viewModel.selectType(type) },
            onSelectAdult = { viewModel.selectAdultAudience() },
            onSelectChild = { viewModel.selectChildAudience() },
            onBackToType = { viewModel.backToType() },
            onBackToAudience = { viewModel.backToAudience() },
            onSelectTheme = { category -> viewModel.selectThemeCategory(category) }
        )
    }
}

@Composable
private fun HelpWizardDialog(
    step: HelpStep,
    onDismiss: () -> Unit,
    onSelectType: (String) -> Unit,
    onSelectAdult: () -> Unit,
    onSelectChild: () -> Unit,
    onBackToType: () -> Unit,
    onBackToAudience: () -> Unit,
    onSelectTheme: (CategoryDto) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Помощь подписчику",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            when (step) {
                HelpStep.SelectType -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Шаг 1 из 3. Выберите тип издания:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onSelectType("JOURNAL") }
                        ) {
                            Text(text = "Журнал")
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onSelectType("NEWSPAPER") }
                        ) {
                            Text(text = "Газета")
                        }
                    }
                }

                is HelpStep.SelectAudience -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Шаг 2 из 3. Кому вы оформляете подписку?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onSelectAdult
                        ) {
                            Text(text = "Взрослому")
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onSelectChild
                        ) {
                            Text(text = "Ребенку")
                        }
                    }
                }

                is HelpStep.SelectTheme -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Шаг 3 из 3. Выберите тематику:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        step.childCategories.forEach { category ->
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = { onSelectTheme(category) }
                            ) {
                                Text(text = category.name)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            when (step) {
                HelpStep.SelectType -> {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Закрыть")
                    }
                }

                is HelpStep.SelectAudience -> {
                    TextButton(onClick = onBackToType) {
                        Text(text = "Назад")
                    }
                }

                is HelpStep.SelectTheme -> {
                    TextButton(onClick = onBackToAudience) {
                        Text(text = "Назад")
                    }
                }
            }
        }
    )
}

