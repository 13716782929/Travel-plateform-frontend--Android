package iss.nus.edu.sg.mygo.api.service

import iss.nus.edu.sg.mygo.api.models.AccommodationResponse
import iss.nus.edu.sg.mygo.api.models.AttractionResponse
import iss.nus.edu.sg.mygo.models.Attraction
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface HotelApiService {
    @GET("content/common/v2/search")
    suspend fun fetchHotel(
        @Query("offset") offset: Int = 0, // default offset = 0
        @Query("limit") limit: Int = 6, // default limit = 6
        @Query("dataset") dataset: String = "accommodation",
        @Header("X-API-Key") apiKey: String,
        @Header("X-Content-Language") contentLanguage: String = "en"
    ): Response<AccommodationResponse>

    @GET("content/accommodation/v2/search")
    suspend fun fetchHotelByUUID(
        @Query("searchType") searchType: String = "uuids",
        @Query("searchValues") uuid: String,
        @Header("X-API-Key") apiKey: String,
        @Header("X-Content-Language") contentLanguage: String = "en"
    ): Response<AccommodationResponse>

    @GET("content/common/v2/search")
    suspend fun fetchHotelByKeyword(
        @Query("keyword") keyword: String,
        @Query("dataset") dataset: String = "accommodation", // 默认查询 accommodation 数据集
        @Query("offset") offset: Int = 0, // 用于分页
        @Query("limit") limit: Int = 7, // 每次加载条目数量
        @Header("X-API-Key") apiKey: String,
        @Header("X-Content-Language") contentLanguage: String = "en"
    ): Response<AccommodationResponse>

    companion object {
        private const val BASE_URL = "https://api.stb.gov.sg" // 确保这个 URL 正确

        fun create(): HotelApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // 解析 JSON
                .build()

            return retrofit.create(HotelApiService::class.java)
        }
    }
}