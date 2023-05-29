package com.ayadiyulianto.dailycoding.data

import androidx.paging.PagingSource
import com.ayadiyulianto.dailycoding.data.local.entity.Story
import com.ayadiyulianto.dailycoding.data.local.room.StoryDao

class FakeStoryDao: StoryDao {

    private var storyData = mutableListOf<Story>()

    override suspend fun insert(stories: List<Story>) {
        TODO("Not yet implemented")
//        storyData.addAll(stories)
    }

    override fun getAll(): PagingSource<Int, Story> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
//        storyData.clear()
    }
}