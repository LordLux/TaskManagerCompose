package com.lordlux.composefirst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.Font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.platform.LocalContext
import com.lordlux.composefirst.ui.theme.ComposeFirstTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

// Imports from app
import com.lordlux.composefirst.ui.theme.ComposeFirstTheme

// Imports for swipe-to-dismiss (from androidx.compose.material)
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ComposeFirstTheme {
				ComposeFirstApp()
			}
		}
	}
}

@Composable
fun ComposeFirstApp() {
	MaterialTheme {
		MainScreen()
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
	val context = LocalContext.current
	val notes = remember { mutableStateListOf<Note>() }
	var nextId by remember { mutableStateOf(0) }
	var showDialog by remember { mutableStateOf(false) }
	var loading by remember { mutableStateOf(true) }

	// Load notes from disk
	LaunchedEffect(Unit) {
		loading = true
		val savedNotes = NoteRepository.loadNotes(context)
		if (savedNotes.isNotEmpty()) {
			notes.addAll(savedNotes)
			nextId = savedNotes.maxOf { it.id } + 1
		} else {
			// Optionally, load some default notes if none are saved.
			notes.addAll(listOf(Note(1, "Note #1", "Desc 1"), Note(2, "Note #2"), Note(3, "Note #3", "Desc 3")))
			nextId = 4
		}
		loading = false
	}

	// Save notes to disk
	LaunchedEffect(notes) {
		snapshotFlow { notes.toList() }.collect { currentNotes ->
			NoteRepository.saveNotes(context, currentNotes)
		}
	}

	// Function to add a note
	fun addNote(title: String, desc: String = "") {
		notes.add(Note(id = nextId, title = title, desc = desc))
		nextId++
	}

	// Main screen layout
	Scaffold(
		topBar = { TopAppBar(title = { Text("Compose First") }) },
		floatingActionButton = {
			FloatingActionButton(
				onClick = { showDialog = true },
				content = { Icon(Icons.Filled.Add, contentDescription = "Add") },
			)
		},
	) { paddingValues ->
		Box(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize(),
			contentAlignment = Alignment.TopCenter,
		) {
			if (loading) CircularProgressIndicator(modifier = Modifier.padding(36.dp))
			else LazyColumn(
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize()
			) {
				items(
					items = notes,
					key = { note -> note.id },
				) { note ->
					NoteItem(note = note, onDismiss = { notes.remove(note) })
				}
				if (notes.isEmpty()) {
					item {
						Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
							Text(text = "There are no notes yet", textAlign = TextAlign.Center)
						}
					}
				}
			}
		}
	}
	if (showDialog) {
		NewTaskDialog(
			onDismiss = { showDialog = false },
			onConfirm = { name, desc ->
				addNote(name, desc)
				showDialog = false
			},
		)
	}
}

// Data class for the note
data class Note(val id: Int, val title: String, val desc: String = "")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(note: Note, onDismiss: () -> Unit) {
	val dismissState = rememberDismissState(confirmStateChange = { dismissValue ->
		dismissValue == DismissValue.DismissedToEnd || dismissValue == DismissValue.DismissedToStart
	})

	LaunchedEffect(dismissState.currentValue) {
		if (dismissState.currentValue == DismissValue.DismissedToEnd || dismissState.currentValue == DismissValue.DismissedToStart) {
			// Delay before removing to match animation duration
			onDismiss() // Remove the note after the animation completes
		}
	}

	SwipeToDismiss(state = dismissState, directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart), background = {
		val bgcolor = if (dismissState.targetValue == DismissValue.Default) MaterialTheme.colorScheme.background
		else MaterialTheme.colorScheme.error
		val iconcolor = if (dismissState.targetValue != DismissValue.Default) MaterialTheme.colorScheme.onError
		else MaterialTheme.colorScheme.error

		BgDeleteBox(bgcolor, iconcolor)
	}, dismissContent = {
		Card(
			modifier = Modifier
				.padding(8.dp)
				.border(1.dp, MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
		) {
			Column(
				modifier = Modifier
					.padding(16.dp)
					.fillMaxWidth()
			) {
				Text(note.title, style = MaterialTheme.typography.bodyLarge)
				if (note.desc.isNotBlank()) Text(note.desc, style = MaterialTheme.typography.bodyMedium)
			}
		}
	})
}


@Composable
fun BgDeleteBox(bgcolor: Color, iconcolor: Color) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(8.dp)
			.background(bgcolor, shape = RoundedCornerShape(12.dp))
			.border(1.dp, iconcolor, RoundedCornerShape(12.dp)), contentAlignment = Alignment.CenterStart
	) {
		Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = iconcolor, modifier = Modifier.padding(16.dp))
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
	var taskName by remember { mutableStateOf("") }
	var taskDesc by remember { mutableStateOf("") }

	val isConfirmEnabled = taskName.isNotBlank() && taskName.length <= 50 && taskDesc.length <= 200

	AlertDialog(
		onDismissRequest = onDismiss, title = { Text("New Task") },
		text = {
			Column {
				OutlinedTextField(
					value = taskName,
					onValueChange = { taskName = it },
					label = { Text("Task Name") },
					modifier = Modifier.fillMaxWidth(),
					isError = taskName.isBlank() || taskName.length > 50,
				)
				if (taskName.isBlank()) {
					Text(text = "Task name cannot be empty", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
				} else if (taskName.length > 50) {
					Text(text = "Task name too long (max 50 characters)", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
				}
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedTextField(
					value = taskDesc,
					onValueChange = { taskDesc = it },
					label = { Text("Description") },
					modifier = Modifier.fillMaxWidth(),
					isError = taskDesc.length > 200,
				)
				if (taskDesc.length > 200) {
					Text(text = "Description too long (max 200 characters)", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
				}
			}
		},
		confirmButton = {
			TextButton(
				onClick = {
					onConfirm(taskName, taskDesc)
				},
				enabled = isConfirmEnabled,
			) {
				Text("Add")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Cancel")
			}
		},
	)
}