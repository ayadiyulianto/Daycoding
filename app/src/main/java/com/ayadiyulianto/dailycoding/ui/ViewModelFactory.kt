package com.ayadiyulianto.dailycoding.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayadiyulianto.dailycoding.data.AppPreferences
import com.ayadiyulianto.dailycoding.di.Injection
import com.ayadiyulianto.dailycoding.ui.auth.AuthViewModel
import com.ayadiyulianto.dailycoding.ui.story.StoryViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val pref = AppPreferences.getInstance(context.dataStore)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(pref, Injection.provideAuthRepository(context)) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(Injection.provideStoryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}