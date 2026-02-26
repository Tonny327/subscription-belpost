package com.belpost.subscription.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.presentation.components.RememberSnackbarState
import com.belpost.subscription.presentation.components.rememberSnackbarState
import com.belpost.subscription.presentation.viewmodel.ProfileViewModel
import com.belpost.subscription.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    isLoggedIn: Boolean,
    onNavigateToLogin: () -> Unit
) {
    val profileState by viewModel.userProfileState.collectAsState()
    val historyState by viewModel.subscriptionHistoryState.collectAsState()
    val snackbarState: RememberSnackbarState = rememberSnackbarState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val nameState = remember { mutableStateOf(TextFieldValue("")) }
    val phoneState = remember { mutableStateOf(TextFieldValue("")) }
    val emailState = remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.loadUserProfile()
            viewModel.loadSubscriptionHistory()
        }
    }

    LaunchedEffect(profileState) {
        if (profileState is UiState.Success) {
            val profile = (profileState as UiState.Success).data
            nameState.value = TextFieldValue(profile.fullName.orEmpty())
            phoneState.value = TextFieldValue(profile.phone.orEmpty())
            emailState.value = TextFieldValue(profile.email.orEmpty())
        }
    }

    LaunchedEffect(snackbarMessage) {
        val message = snackbarMessage
        if (!message.isNullOrBlank()) {
            snackbarState.showMessage(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Личный кабинет") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isLoggedIn) {
                Text(text = "Войдите в аккаунт для доступа к личному кабинету")
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToLogin
                ) {
                    Text("Войти / Зарегистрироваться")
                }
            } else {
            Text(text = "Профиль")

            when (profileState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateUserProfile(
                                    fullName = nameState.value.text,
                                    phone = phoneState.value.text,
                                    email = emailState.value.text
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Сохранить")
                        }
                        TextButton(onClick = { viewModel.logout() }) {
                            Text(text = "Выйти")
                        }
                    }
                }

                is UiState.Error -> {
                    Text(text = "Не удалось загрузить профиль")
                }
            }

            Text(text = "История подписок")

            when (historyState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val items = (historyState as UiState.Success<List<SubscriptionResponseDto>>).data
                    if (items.isEmpty()) {
                        Text(text = "Подписок пока нет")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(items) { item ->
                                SubscriptionHistoryItem(
                                    item = item,
                                    onCancel = {
                                        if (item.status == "ACTIVE") {
                                            viewModel.cancelSubscription(item.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Text(text = "Не удалось загрузить историю подписок")
                }
            }
            }
        }
    }
}

@Composable
private fun SubscriptionHistoryItem(
    item: SubscriptionResponseDto,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = item.publication.title)
            Text(text = "Период: ${item.period}")
            Text(text = "Даты: ${item.startDate} – ${item.endDate}")

            val statusColor = when (item.status) {
                "ACTIVE" -> Color(0xFF2E7D32)
                "CANCELLED" -> Color(0xFFC62828)
                "EXPIRED" -> Color(0xFF757575)
                else -> Color.Unspecified
            }
            Text(
                text = "Статус: ${item.status}",
                color = statusColor
            )
            Text(text = "Сумма: ${"%.2f".format(item.totalPrice)} руб.")

            if (item.status == "ACTIVE") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancel) {
                        Text(text = "Отменить")
                    }
                }
            }
        }
    }
}

