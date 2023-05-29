package com.ayadiyulianto.dailycoding.ui.story

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ayadiyulianto.dailycoding.data.Result
import com.ayadiyulianto.dailycoding.data.StoryRepository
import com.ayadiyulianto.dailycoding.data.local.entity.Story
import java.io.File

class StoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private var token: String = ""
    private var location: Location? = null

    fun setToken(_token: String) {
        token = _token
    }

    fun setLocation(_location: Location) {
        location = _location
    }

    fun stories() = storyRepository.getStories(token)

    fun storiesData(): LiveData<PagingData<Story>> {
        return storyRepository.getStoriesData(token).cachedIn(viewModelScope)
    }

    fun story(id: Int): LiveData<Result<Story>> {
        return storyRepository.getStory(token, id)
    }

    fun uploadStory(file: File, description: String, allowLocation: Boolean): LiveData<Result<String>> {
        return if (allowLocation) {
            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0
            storyRepository.uploadStory(token, file, description, lat, lon)
        } else {
            storyRepository.uploadStory(token, file, description)
        }
    }
}