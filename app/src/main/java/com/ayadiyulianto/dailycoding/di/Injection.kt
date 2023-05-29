package com.ayadiyulianto.dailycoding.di

import android.content.Context
import com.ayadiyulianto.dailycoding.data.AuthRepository
import com.ayadiyulianto.dailycoding.data.StoryRepository
import com.ayadiyulianto.dailycoding.data.local.room.AppDatabase
import com.ayadiyulianto.dailycoding.data.remote.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val database = AppDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService,database)
    }
}