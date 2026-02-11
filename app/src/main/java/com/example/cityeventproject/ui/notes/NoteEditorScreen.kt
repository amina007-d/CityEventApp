package com.example.cityeventproject.ui.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityeventproject.ui.components.SimpleTopBar

@Composable
fun NoteEditorScreen(
    noteId: String?,
    onBack: () -> Unit,
    vm: NoteEditorViewModel = hiltViewModel()
) {
    val title by vm.title.collectAsState()
    val text by vm.text.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(noteId) { vm.load(noteId) }

    Column(Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = if (noteId.isNullOrBlank()) "New Note" else "Edit Note",
            onBack = onBack
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (loading) {
                CircularProgressIndicator()
            }

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = vm::setTitle,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = text,
                onValueChange = vm::setText,
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { vm.save(noteId) { onBack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (noteId.isNullOrBlank()) "Create" else "Save")
            }
        }
    }
}
