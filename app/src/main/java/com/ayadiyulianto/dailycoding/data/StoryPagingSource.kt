package com.ayadiyulianto.dailycoding.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ayadiyulianto.dailycoding.data.local.entity.Story
import com.ayadiyulianto.dailycoding.data.remote.response.StoriesResponse
import com.ayadiyulianto.dailycoding.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val storyList = ArrayList<Story>()

            val client = apiService.getStories(token, page, params.loadSize)
            client.enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val stories = loginResponse?.listStory
                        stories?.forEach { item ->
                            val story = Story(
                                item.id,
                                item.name,
                                item.description,
                                item.photoUrl,
                                item.createdAt,
                                item.lat,
                                item.lon
                            )
                            storyList.add(story)
                        }
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    //
                }
            })

            LoadResult.Page(
                data = storyList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (storyList.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}