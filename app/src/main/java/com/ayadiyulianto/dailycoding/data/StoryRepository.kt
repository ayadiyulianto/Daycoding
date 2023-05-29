package com.ayadiyulianto.dailycoding.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ayadiyulianto.dailycoding.data.local.entity.Story
import com.ayadiyulianto.dailycoding.data.local.room.AppDatabase
import com.ayadiyulianto.dailycoding.data.remote.response.BaseResponse
import com.ayadiyulianto.dailycoding.data.remote.response.StoriesResponse
import com.ayadiyulianto.dailycoding.data.remote.response.StoryResponse
import com.ayadiyulianto.dailycoding.data.remote.retrofit.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) {
    private val resultStories = MediatorLiveData<Result<List<Story>>>()

    fun getStory(_token: String, id: Int): LiveData<Result<Story>> {
        val result =  MediatorLiveData<Result<Story>>()
        result.value = Result.Loading
        val token = "Bearer $_token"
        val client = apiService.getStory(token, id)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    res?.story?.let { item ->
                        val story = Story(
                            item.id,
                            item.name,
                            item.description,
                            item.photoUrl,
                            item.createdAt,
                            item.lat,
                            item.lon
                        )
                        result.value = Result.Success(story)
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return result
    }

    fun getStoriesData(_token: String): LiveData<PagingData<Story>> {
        val token = "Bearer $_token"
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(appDatabase, apiService, token),
            pagingSourceFactory = {
                //StoryPagingSource(apiService, token)
                appDatabase.storyDao().getAll()
            }
        ).liveData
    }

    fun getStories(_token: String): LiveData<Result<List<Story>>> {
        resultStories.value = Result.Loading
        val token = "Bearer $_token"
        val client = apiService.getStories(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    val stories = res?.listStory
                    val storyList = ArrayList<Story>()
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
                    resultStories.value = Result.Success(storyList)
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                resultStories.value = Result.Error(t.message.toString())
            }
        })
        return resultStories
    }

    fun uploadStory(
        _token: String,
        _photo: File,
        _description: String,
        _lat: Double? = null,
        _lon: Double? = null
    ): LiveData<Result<String>> {
        val result = MediatorLiveData<Result<String>>()
        result.value = Result.Loading
        val token = "Bearer $_token"

        val description = _description.toRequestBody("text/plain".toMediaType())
//        val lat = _lat.toString().toRequestBody("do".toMediaType())
//        val lon = _lon.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = _photo.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            _photo.name,
            requestImageFile
        )
        val client = apiService.uploadStory(token, imageMultipart, description, _lat, _lon)
        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    val message = registerResponse?.message
                    if (message != null) {
                        result.value = Result.Success(message)
                    }
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<BaseResponse>() {}.type
                    val baseResponse: BaseResponse? =
                        gson.fromJson(response.errorBody()!!.string(), type)
                    if (baseResponse != null) {
                        result.value = Result.Error(baseResponse.message)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return result
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            appDatabase: AppDatabase
            // appExecutors: AppExecutors
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, appDatabase)
            }.also { instance = it }
    }
}