package com.ayadiyulianto.dailycoding.data

import com.ayadiyulianto.dailycoding.data.remote.response.BaseResponse
import com.ayadiyulianto.dailycoding.data.remote.response.LoginRequest
import com.ayadiyulianto.dailycoding.data.remote.response.LoginResponse
import com.ayadiyulianto.dailycoding.data.remote.response.RegisterRequest
import com.ayadiyulianto.dailycoding.data.remote.response.StoriesResponse
import com.ayadiyulianto.dailycoding.data.remote.response.StoryResponse
import com.ayadiyulianto.dailycoding.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class FakeApiService: ApiService {
    override fun login(loginRequest: LoginRequest): Call<LoginResponse> {
        TODO("Not yet implemented")
    }

    override fun register(registerRequest: RegisterRequest): Call<BaseResponse> {
        TODO("Not yet implemented")
    }

    override fun getStory(token: String, id: Int): Call<StoryResponse> {
        TODO("Not yet implemented")
    }

    override fun getStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int
    ): Call<StoriesResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getStoriesWithLoc(
        token: String,
        page: Int?,
        size: Int?,
        location: Int
    ): StoriesResponse {
        TODO("Not yet implemented")
    }

    override fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ): Call<BaseResponse> {
        TODO("Not yet implemented")
    }
}