package com.example.diaryapp.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferencesManager(private val context: Context) {
    private val FONT_SIZE_KEY = intPreferencesKey("font_size")

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { prefs ->
            prefs[FONT_SIZE_KEY] = size
        }
    }

    fun getFontSize(): Flow<Int> =
        context.dataStore.data.map { it[FONT_SIZE_KEY] ?: 16 }
}