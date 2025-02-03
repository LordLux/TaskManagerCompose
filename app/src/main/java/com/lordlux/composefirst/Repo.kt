package com.lordlux.composefirst

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

// Create an extension property for DataStore in Context.
val Context.dataStore by preferencesDataStore(name = "notes_prefs")

object NoteRepository {
	private val NOTES_KEY = stringPreferencesKey("notes")
	private val gson = Gson()

	// Save the list of notes as a JSON string.
	suspend fun saveNotes(context: Context, notes: List<Note>) {
		val json = gson.toJson(notes)
		context.dataStore.edit { preferences ->
			preferences[NOTES_KEY] = json
		}
	}

	// Load the list of notes. If no value is saved, return an empty list.
	suspend fun loadNotes(context: Context): List<Note> {
		val preferences = context.dataStore.data.first()
		val json = preferences[NOTES_KEY] ?: return emptyList()
		val type = object : TypeToken<List<Note>>() {}.type
		return gson.fromJson(json, type)
	}
}
