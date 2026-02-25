package com.belpost.subscription.presentation.screens.detail

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.belpost.subscription.R
import com.belpost.subscription.data.api.models.PublicationDto
import com.belpost.subscription.data.api.models.SubscriptionRequestDto
import com.belpost.subscription.data.api.models.SubscriptionResponseDto
import com.belpost.subscription.presentation.components.RememberSnackbarState
import com.belpost.subscription.presentation.components.rememberSnackbarState
import com.belpost.subscription.presentation.subscription.SubscriptionPeriod
import com.belpost.subscription.presentation.subscription.calculateSubscriptionPrice
import com.belpost.subscription.presentation.viewmodel.SubscriptionViewModel
import com.belpost.subscription.utils.UiState
import java.time.LocalDate

@Composable
fun DetailScreen(
    publication: PublicationDto,
    viewModel: SubscriptionViewModel,
    onBack: () -> Unit,
    onAddToCart: (PublicationDto, SubscriptionPeriod) -> Unit,
    onSubscriptionSuccess: (SubscriptionResponseDto) -> Unit
) {
    val nameState = remember { mutableStateOf(TextFieldValue("")) }
    val phoneState = remember { mutableStateOf(TextFieldValue("")) }
    val emailState = remember { mutableStateOf(TextFieldValue("")) }
    val selectedPeriodState = remember { mutableStateOf(SubscriptionPeriod.ONE_MONTH) }
    val subscriptionState by viewModel.subscriptionState.collectAsState()
    val snackbarState: RememberSnackbarState = rememberSnackbarState()

    LaunchedEffect(subscriptionState) {
        when (val state = subscriptionState) {
            is UiState.Success -> {
                onSubscriptionSuccess(state.data)
            }
            is UiState.Error -> {
                snackbarState.showMessage(state.message)
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = publication.imageUrl,
            contentDescription = publication.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_publication_placeholder),
            error = painterResource(id = R.drawable.ic_publication_placeholder),
            fallback = painterResource(id = R.drawable.ic_publication_placeholder)
        )

        Text(text = publication.title)
        publication.description?.let { Text(text = it) }
        Text(text = "${publication.price} руб. / ${publication.period}")

        val totalPrice = calculateSubscriptionPrice(
            basePrice = publication.price,
            period = selectedPeriodState.value
        )

        Text(
            text = "Период: ${selectedPeriodState.value.label}",
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Итоговая стоимость: ${"%.2f".format(totalPrice)} руб.",
            modifier = Modifier.fillMaxWidth()
        )

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

        if (subscriptionState is UiState.Loading) {
            CircularProgressIndicator()
        }

        Button(
            onClick = {
                val request = SubscriptionRequestDto(
                    publicationId = publication.id,
                    customerName = nameState.value.text,
                    customerPhone = phoneState.value.text,
                    customerEmail = emailState.value.text,
                    startDate = LocalDate.now().toString(),
                    period = selectedPeriodState.value.label
                )
                viewModel.createSubscription(request)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Оформить подписку")
        }

        Button(
            onClick = { onAddToCart(publication, selectedPeriodState.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Добавить в корзину")
        }
    }
}

