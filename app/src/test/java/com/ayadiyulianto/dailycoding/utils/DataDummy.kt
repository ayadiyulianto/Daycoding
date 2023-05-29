package com.ayadiyulianto.dailycoding.utils

import com.ayadiyulianto.dailycoding.data.remote.response.StoriesItem
import com.ayadiyulianto.dailycoding.data.remote.response.StoriesResponse

object DataDummy {

    fun generateDummyStoriesResponse(): StoriesResponse {
        val items: MutableList<StoriesItem> = arrayListOf()
        for (i in 0..100) {
            val quote = StoriesItem(
                i.toString(),
                "author + $i",
                "quote $i",
                "https://www.dicoding.com",
                "2022-02-02T10:10:10Z",
                0.0,
                0.0
            )
            items.add(quote)
        }
        return StoriesResponse(false, "successfully", items)
    }
}