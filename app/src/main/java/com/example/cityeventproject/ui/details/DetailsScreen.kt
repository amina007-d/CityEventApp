package com.example.cityeventproject.ui.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cityeventproject.ui.components.SimpleTopBar

@Composable
fun DetailsScreen(
    eventId: String,
    onBack: () -> Unit,
    onOpenComments: () -> Unit,
    vm: DetailsViewModel = hiltViewModel()
) {
    val event by vm.event.collectAsState()
    val err by vm.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        vm.load(eventId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(title = "Details", onBack = onBack)

        when {
            err != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(text = err!!) }
            }

            event == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            else -> {
                val e = event!!

                val dateTime = e.date + (e.time?.let { " • $it" } ?: "")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!e.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = e.imageUrl,
                            contentDescription = e.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                    }

                    Text(text = e.name, style = MaterialTheme.typography.headlineSmall)
                    Text(text = dateTime)

                    Text(
                        text = listOfNotNull(e.city, e.venue, e.address).joinToString(" • "),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (!e.category.isNullOrBlank()) {
                        Text(text = "Category: ${e.category}")
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onOpenComments) {
                            Text("Comments")
                        }

                        Button(onClick = { vm.toggleFavorite(e.id, !e.isFavorite) }) {
                            Text(if (e.isFavorite) "Remove favorite" else "Add favorite")
                        }

                        if (!e.url.isNullOrBlank()) {
                            OutlinedButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(e.url))
                                context.startActivity(intent)
                            }) {
                                Text("Open web")
                            }
                        }
                    }
                }
            }
        }
    }
}
