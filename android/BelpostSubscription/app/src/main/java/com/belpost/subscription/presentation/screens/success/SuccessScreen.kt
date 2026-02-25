package com.belpost.subscription.presentation.screens.success

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.belpost.subscription.data.api.models.SubscriptionResponseDto

@Composable
fun SuccessScreen(
    subscription: SubscriptionResponseDto,
    onGoHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Подписка оформлена!")
        Text(text = "Номер: ${subscription.id}")
        Text(text = "Издание: ${subscription.publication.title}")
        Text(text = "Период: ${subscription.period}")
        Text(text = "Итоговая сумма: ${subscription.totalPrice} руб.")

        Button(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "На главную")
        }
    }
}

