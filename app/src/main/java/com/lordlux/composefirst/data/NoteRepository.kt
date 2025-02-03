package com.lordlux.composefirst.data

import android.content.Context
import com.lordlux.composefirst.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object NoteRepository {
	private const val FILE_NAME = "notes.json"

	suspend fun loadNotes(context: Context): List<Note> = withContext(Dispatchers.IO) {
		val file = File(context.filesDir, FILE_NAME)
		if (file.exists()) {
			val json = file.readText()
			// Deserialize JSON to List<Note>
			// Assuming you have a JSON library like kotlinx.serialization or Gson
			// return Json.decodeFromString(json)
			// For simplicity, returning an empty list here
			emptyList()
		} else {
			emptyList()
		}
	}

	suspend fun saveNotes(context: Context, notes: List<Note>) = withContext(Dispatchers.IO) {
		val file = File(context.filesDir, FILE_NAME)
		// Serialize notes to JSON
		// val json = Json.encodeToString(notes)
		// file.writeText(json)
	}
}