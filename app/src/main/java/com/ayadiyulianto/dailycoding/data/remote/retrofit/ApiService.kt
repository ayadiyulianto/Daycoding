package com.ayadiyulianto.dailycoding.data.remote.retrofit

import com.ayadiyulianto.dailycoding.data.remote.response.BaseResponse
import com.ayadiyulianto.dailycoding.data.remote.response.LoginRequest
import com.ayadiyulianto.dailycoding.data.remote.response.LoginResponse
import com.ayadiyulianto.dailycoding.data.remote.response.RegisterRequest
import com.ayadiyulianto.dailycoding.data.remote.response.StoriesResponse
import com.ayadiyulianto.dailycoding.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // AUTH

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<BaseResponse>

    // STORIES

    @GET("stories/{id}")
    fun getStory(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<StoryResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 1
    ): Call<StoriesResponse>

    @GET("stories")
    suspend fun getStoriesWithLoc(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 1
    ): StoriesResponse

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): Call<BaseResponse>
}