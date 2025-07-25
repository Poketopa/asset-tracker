package com.hyunsung.assettracker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Response

const val BASE_URL = "http://10.0.2.2:8080"

interface TestApi {
    @GET("/api/test")
    suspend fun getTestMessage(): Response<TestMessage>
}

data class TestMessage(
    val message: String
)

object RetrofitClient {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val testApi: TestApi = retrofit.create(TestApi::class.java)
} 