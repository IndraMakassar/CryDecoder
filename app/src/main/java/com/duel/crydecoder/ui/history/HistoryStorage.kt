package com.duel.crydecoder.ui.history

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "history_store")

class HistoryStorage(private val context: Context) {
    private val gson = Gson()
    private val key = stringPreferencesKey("history_json")

    suspend fun save(items: List<HistoryUiState>) {
        val json = gson.toJson(items)
        context.dataStore.edit { prefs ->
            prefs[key] = json
        }
    }

    suspend fun load(): List<HistoryUiState> {
        val prefs = context.dataStore.data.first()
        val json = prefs[key] ?: return emptyList()
        val type = object : TypeToken<List<HistoryUiState>>() {}.type
        return gson.fromJson(json, type)
    }
}