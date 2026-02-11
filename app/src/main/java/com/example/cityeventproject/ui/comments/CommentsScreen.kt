package com.example.cityeventproject.ui.comments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityeventproject.domain.model.Comment
import com.example.cityeventproject.ui.components.SimpleTopBar

@Composable
fun CommentsScreen(
    eventId: String,
    onBack: () -> Unit,
    vm: CommentsViewModel = hiltViewModel()
) {
    val commentsState = remember(eventId) { vm.observe(eventId) }
    val comments by commentsState.collectAsState()
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar(title = "Comments", onBack = onBack)
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Write a comment") },
                supportingText = { Text("3..280 characters. Realtime CRUD via Firebase Realtime Database.") },
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) Text(error!!)

            Button(onClick = {
                error = null
                vm.add(eventId, input) { msg -> error = msg }
                if (error == null) input = ""
            }) { Text("Post") }

            Divider()

            if (comments.isEmpty()) {
                Text("No comments yet. Be the first!")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(comments.size) { idx ->
                        CommentCard(
                            c = comments[idx],
                            onEdit = { id, text -> vm.update(eventId, id, text) { msg -> error = msg } },
                            onDelete = { id -> vm.delete(eventId, id) { msg -> error = msg } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentCard(c: Comment, onEdit: (String, String) -> Unit, onDelete: (String) -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(c.text) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("${c.displayName} • ${java.text.DateFormat.getDateTimeInstance().format(java.util.Date(c.createdAt))}", style = MaterialTheme.typography.bodySmall)
            if (!editing) {
                Text(c.text)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { editing = true }) { Text("Edit") }
                    TextButton(onClick = { onDelete(c.id) }) { Text("Delete") }
                }
            } else {
                OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onEdit(c.id, text); editing = false }) { Text("Save") }
                    OutlinedButton(onClick = { editing = false; text = c.text }) { Text("Cancel") }
                }
            }
        }
    }
}
