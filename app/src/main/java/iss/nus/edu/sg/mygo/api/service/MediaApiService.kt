package iss.nus.edu.sg.mygo.api.service

import iss.nus.edu.sg.mygo.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface MediaApiService {
    @GET("/proxy/media/{uuid}")
    suspend fun getImageUrl(@Path("uuid") uuid: String): String

    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL // 本机后端地址

        fun create(): MediaApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MediaApiService::class.java)
        }
    }
}
