package com.belpost.subscription.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.belpost.subscription.presentation.viewmodel.AuthViewModel
import com.belpost.subscription.utils.UiState

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is UiState.Success -> {
                viewModel.clearLoginState()
                onLoginSuccess()
            }
            else -> Unit
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Вход в аккаунт", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.login(email, password) },
                enabled = loginState !is UiState.Loading
            ) {
                if (loginState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp))
                } else {
                    Text("Войти")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Нет аккаунта? Зарегистрироваться")
            }

            (loginState as? UiState.Error)?.message?.let { msg ->
                Text(text = msg, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
        }
    }
}
