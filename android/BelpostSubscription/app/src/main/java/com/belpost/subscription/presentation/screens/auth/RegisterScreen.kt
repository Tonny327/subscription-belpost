package com.belpost.subscription.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is UiState.Success -> {
                viewModel.clearRegisterState()
                onRegisterSuccess()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Регистрация") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль (мин. 6 символов)") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            (registerState as? UiState.Error)?.message?.let { msg ->
                Text(text = msg, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.register(fullName, email, phone, password) },
                enabled = registerState !is UiState.Loading
            ) {
                if (registerState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp))
                } else {
                    Text("Зарегистрироваться")
                }
            }
        }
    }
}
