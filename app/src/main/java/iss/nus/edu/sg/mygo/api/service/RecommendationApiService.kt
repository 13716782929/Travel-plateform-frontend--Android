package iss.nus.edu.sg.mygo.api.service

/**
 * @Description
 * @Author YAO YIYANG
 * @StudentID A0294873L
 * @Date 2025/2/11
 * @Version 1.0
 */

import iss.nus.edu.sg.mygo.api.models.AttractionRecommendation
import iss.nus.edu.sg.mygo.models.Attraction
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RecommendationApiService {
    @GET("/api/recommendations/personalized/{userId}")
    suspend fun fetchPersonalizedAttractions(@Path("userId") userId: Int): Response<List<AttractionRecommendation>>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"  // 本地服务器地址

        fun create(): RecommendationApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RecommendationApiService::class.java)
        }
    }
}
