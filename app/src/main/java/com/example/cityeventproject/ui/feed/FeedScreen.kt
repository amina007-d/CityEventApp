package com.example.cityeventproject.ui.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.ui.components.SimpleTopBar

@Composable
fun FeedScreen(
    onOpenSearch: () -> Unit,
    onOpenDetails: (String) -> Unit,
    onOpenProfile: () -> Unit,
    vm: FeedViewModel
) {
    val items = vm.paging.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = "City Events",
            actions = {
                TextButton(onClick = onOpenSearch) { Text("Search") }
                TextButton(onClick = onOpenProfile) { Text("Profile") }
            }
        )

        FeedList(
            items = items,
            onOpenDetails = onOpenDetails,
            onToggleFav = { id, fav -> vm.toggleFavorite(id, fav) }
        )
    }
}

@Composable
private fun FeedList(
    items: LazyPagingItems<Event>,
    onOpenDetails: (String) -> Unit,
    onToggleFav: (String, Boolean) -> Unit
) {
    when {
        items.loadState.refresh is androidx.paging.LoadState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        items.loadState.refresh is androidx.paging.LoadState.Error -> {
            val err = (items.loadState.refresh as androidx.paging.LoadState.Error).error
            val msg = err.message ?: "Unknown error"

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: $msg")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { items.retry() }) { Text("Retry") }
                }
            }
        }

        items.itemCount == 0 -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No events. Try search or change filters.")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(count = items.itemCount, key = items.itemKey { it.id }) { index ->
                    val e = items[index] ?: return@items
                    EventCard(event = e, onOpen = { onOpenDetails(e.id) }, onToggleFav = onToggleFav)
                }

                item {
                    when (items.loadState.append) {
                        is androidx.paging.LoadState.Loading -> {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                CircularProgressIndicator(Modifier.padding(16.dp))
                            }
                        }

                        is androidx.paging.LoadState.Error -> {
                            val err = (items.loadState.append as androidx.paging.LoadState.Error).error
                            val msg = err.message ?: "Unknown error"

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Load more failed: $msg")
                                    TextButton(onClick = { items.retry() }) { Text("Retry") }
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: Event,
    onOpen: () -> Unit,
    onToggleFav: (String, Boolean) -> Unit
) {
    val dateTime = event.date + (event.time?.let { " • $it" } ?: "")

    Card(modifier = Modifier.fillMaxWidth().clickable { onOpen() }) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!event.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.name,
                    modifier = Modifier.size(88.dp)
                )
                Spacer(Modifier.width(12.dp))
            }

            Column(Modifier.weight(1f)) {
                Text(event.name, style = MaterialTheme.typography.titleMedium)
                Text(text = dateTime, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = listOfNotNull(event.city, event.venue).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = { onToggleFav(event.id, !event.isFavorite) }) {
                Icon(
                    imageVector = if (event.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
        }
    }
}
