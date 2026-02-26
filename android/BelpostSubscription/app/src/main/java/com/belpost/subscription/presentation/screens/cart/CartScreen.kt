package com.belpost.subscription.presentation.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.belpost.subscription.presentation.cart.CartItem
import com.belpost.subscription.presentation.components.RememberSnackbarState
import com.belpost.subscription.presentation.components.rememberSnackbarState
import com.belpost.subscription.presentation.subscription.SubscriptionPeriod
import com.belpost.subscription.presentation.viewmodel.CartViewModel
import com.belpost.subscription.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val checkoutState by viewModel.checkoutState.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarState: RememberSnackbarState = rememberSnackbarState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            snackbarState.showMessage(msg)
            viewModel.clearError()
        }
    }

    val nameState = remember { mutableStateOf(TextFieldValue("")) }
    val phoneState = remember { mutableStateOf(TextFieldValue("")) }
    val emailState = remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(checkoutState) {
        when (checkoutState) {
            is UiState.Success -> {
                snackbarState.showMessage("Подписки успешно оформлены")
                viewModel.clearCheckoutState()
                onBack()
            }

            is UiState.Error -> {
                val message = (checkoutState as UiState.Error).message
                snackbarState.showMessage(message)
                viewModel.clearCheckoutState()
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Корзина") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarState.hostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (items.isEmpty()) {
                    Text(text = "Корзина пуста")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(items) { item ->
                            CartItemRow(
                                item = item,
                                onRemove = { viewModel.removeFromCart(item) },
                                onChangePeriod = { newPeriod ->
                                    viewModel.updateItemPeriod(item, newPeriod)
                                }
                            )
                        }
                    }

                    Text(text = "Итого: ${"%.2f".format(totalPrice)} руб.")

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("ФИО") }
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = phoneState.value,
                        onValueChange = { phoneState.value = it },
                        label = { Text("Телефон") }
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
                        label = { Text("Email") }
                    )

                    Button(
                        onClick = {
                            viewModel.checkoutAll(
                                customerName = nameState.value.text,
                                customerPhone = phoneState.value.text,
                                customerEmail = emailState.value.text
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = checkoutState !is UiState.Loading && items.isNotEmpty()
                    ) {
                        Text(text = "Оформить все подписки")
                    }
                }
            }

            if (checkoutState is UiState.Loading || loadingState) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit,
    onChangePeriod: (SubscriptionPeriod) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.publication.title)
                Text(text = "Период: ${item.selectedPeriod.label}")
                Text(text = "Стоимость: ${"%.2f".format(item.calculatedPrice)} руб.")
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить"
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SubscriptionPeriod.values().forEach { period ->
                TextButton(
                    onClick = { onChangePeriod(period) }
                ) {
                    Text(text = period.label)
                }
            }
        }
    }
}

