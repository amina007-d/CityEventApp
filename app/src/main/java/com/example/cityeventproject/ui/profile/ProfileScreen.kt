package com.example.cityeventproject.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.domain.model.UserNote
import com.example.cityeventproject.ui.components.SimpleTopBar
import java.text.DateFormat
import java.util.Date

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenDetails: (String) -> Unit,
    onOpenNoteEditor: (String?) -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    val user by vm.user.collectAsState()
    val favoriteEvents by vm.favoriteEvents.collectAsState()
    val notes by vm.notes.collectAsState()
    val (toast, setToast) = remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar(title = "Profile", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (user == null) {
                            Text("Not signed in.")
                        } else {
                            Text("Signed in as", style = MaterialTheme.typography.labelLarge)
                            Text(user!!.displayName, style = MaterialTheme.typography.titleLarge)
                            Text(user!!.email ?: "", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(onClick = { onOpenNoteEditor(null) }) { Text("New note") }
                                Button(onClick = vm::signOut) { Text("Sign out") }
                            }
                        }
                    }
                }
            }

            if (toast != null) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Text(
                            text = toast!!,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item { SectionHeader("Saved events") }

            if (favoriteEvents.isEmpty()) {
                item {
                    Text(
                        text = "No saved events yet. Tap the heart on an event to save it.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(favoriteEvents, key = { it.id }) { e ->
                    FavoriteEventRow(
                        e = e,
                        onOpen = { onOpenDetails(e.id) },
                        onRemove = { vm.removeFavorite(e.id) { setToast(it) } }
                    )
                }
            }

            item {
                Spacer(Modifier.height(6.dp))
                Divider()
                Spacer(Modifier.height(6.dp))
            }

            item { SectionHeader("My notes") }

            if (notes.isEmpty()) {
                item {
                    Text(
                        text = "No notes yet. Create one to track ideas, plans, or reminders.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(notes, key = { it.id }) { n ->
                    NoteRow(
                        n = n,
                        onEdit = { onOpenNoteEditor(n.id) },
                        onDelete = { vm.deleteNote(n.id) { setToast(it) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun FavoriteEventRow(
    e: Event,
    onOpen: () -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { onOpen() }
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!e.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = e.imageUrl,
                    contentDescription = e.name,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.size(12.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    e.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val dateTime = e.date + (e.time?.let { " • $it" } ?: "")
                if (dateTime.isNotBlank()) Text(dateTime, style = MaterialTheme.typography.bodySmall)
                val place = listOfNotNull(e.city, e.venue).joinToString(" • ")
                if (place.isNotBlank()) Text(place, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onRemove) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_delete),
                    contentDescription = "Remove"
                )
            }
        }
    }
}

@Composable
private fun NoteRow(
    n: UserNote,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(n.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        "Updated: " + DateFormat.getDateTimeInstance().format(Date(n.updatedAt)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                Spacer(Modifier.size(8.dp))
                OutlinedButton(onClick = onDelete) { Text("Delete") }
            }
            Text(n.text, maxLines = 4, overflow = TextOverflow.Ellipsis)
        }
    }
}
