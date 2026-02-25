package com.belpost.subscription.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.belpost.subscription.R
import com.belpost.subscription.data.api.models.PublicationDto

@Composable
fun PublicationList(
    publications: List<PublicationDto>,
    onItemClick: (PublicationDto) -> Unit,
    onAddToCart: (PublicationDto) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        items(publications) { publication ->
            PublicationCard(
                publication = publication,
                onClick = { onItemClick(publication) },
                onAddToCart = { onAddToCart(publication) }
            )
        }
    }
}

@Composable
fun PublicationCard(
    publication: PublicationDto,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = publication.imageUrl,
                contentDescription = publication.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_publication_placeholder),
                error = painterResource(id = R.drawable.ic_publication_placeholder),
                fallback = painterResource(id = R.drawable.ic_publication_placeholder)
            )

            val regionOrCategory = publication.categoryNames?.firstOrNull()
            if (!regionOrCategory.isNullOrBlank()) {
                Text(
                    text = regionOrCategory,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = publication.title,
                style = MaterialTheme.typography.titleLarge
            )

            publication.description?.let { desc ->
                val lines = desc.lines()
                if (lines.isNotEmpty()) {
                    Text(
                        text = lines.first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (lines.size > 1) {
                    Text(
                        text = lines.drop(1).joinToString(" "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${publication.price} руб. / ${publication.period}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                TextButton(onClick = onAddToCart) {
                    Text(text = "В корзину")
                }
            }
        }
    }
}

