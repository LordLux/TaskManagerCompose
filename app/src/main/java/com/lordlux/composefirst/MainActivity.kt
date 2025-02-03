package com.lordlux.composefirst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

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
	val notes = remember { mutableStateListOf(Note(1, "Note #1"), Note(2, "Note #2"), Note(3, "Note #3")) }

	var nextId = remember { notes.size + 1 }

	fun newNote() {
		notes.add(Note(id = nextId++, text = "Note #$nextId"))
	}

	@Composable
	fun NoteList(noteslist: MutableList<Note>, modifier: Modifier = Modifier) {
		LazyColumn(modifier = modifier.fillMaxSize()) {
			items(
				items = notes,
				key = { note -> note.id }  // ✅ Now using unique IDs instead of text
			) { note ->
				NoteItem(note = note, onDismiss = { noteslist.remove(note) })
			}
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(title = { Text("Compose First") })
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = { newNote() },
				content = { Icon(Icons.Filled.Add, contentDescription = "Add") },
			)
		},
	) { paddingValues -> NoteList(notes, Modifier.padding(paddingValues)) }
}

// Data class for the note
data class Note(val id: Int, val text: String)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(note: Note, onDismiss: () -> Unit) {
	val dismissState = rememberDismissState(
		confirmStateChange = { dismissValue ->
			dismissValue == DismissValue.DismissedToEnd || dismissValue == DismissValue.DismissedToStart
		}
	)

	if (dismissState.currentValue == DismissValue.DismissedToEnd ||
		dismissState.currentValue == DismissValue.DismissedToStart) {
		LaunchedEffect(note) {
			// Adjust the delay to match the animation duration (default ~300ms)
			kotlinx.coroutines.delay(300)
			onDismiss()
		}
	}

	SwipeToDismiss(
		state = dismissState,
		directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
		background = {
			val bgcolor =
				if (dismissState.targetValue == DismissValue.Default)
					MaterialTheme.colorScheme.background
				else
					MaterialTheme.colorScheme.error
			val iconcolor =
				if (dismissState.targetValue != DismissValue.Default)
					MaterialTheme.colorScheme.onError
				else
					MaterialTheme.colorScheme.error

			BgDeleteBox(bgcolor, iconcolor)
		},
		dismissContent = {
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
					Text("Note ${note.text}")
					Text("This is the content of ${note.text}")
				}
			}
		}
	)
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