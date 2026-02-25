package com.belpost.subscription.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.presentation.viewmodel.SubscriptionViewModel

@Composable
fun DetailScreen(
    publication: PublicationDto,
    viewModel: SubscriptionViewModel,
    onBack: () -> Unit,
    onSubscriptionSuccess: (SubscriptionResponseDto) -> Unit
) {
    val nameState = remember { mutableStateOf(TextFieldValue("")) }
    val phoneState = remember { mutableStateOf(TextFieldValue("")) }
    val emailState = remember { mutableStateOf(TextFieldValue("")) }
    val periodState = remember { mutableStateOf("1 месяц") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = publication.title)
        publication.description?.let { Text(text = it) }
        Text(text = "${publication.price} руб. / ${publication.period}")

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

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val request = SubscriptionRequestDto(
                    publicationId = publication.id,
                    customerName = nameState.value.text,
                    customerPhone = phoneState.value.text,
                    customerEmail = emailState.value.text,
                    startDate = java.time.LocalDate.now().toString(),
                    period = periodState.value
                )
                viewModel.createSubscription(request)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Оформить подписку")
        }
    }
}

