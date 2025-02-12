package iss.nus.edu.sg.mygo.api.service


import iss.nus.edu.sg.mygo.api.models.AccommodationResponse
import iss.nus.edu.sg.mygo.api.models.AttractionResponse
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.Review
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AttractionApiService {
    @GET("content/common/v2/search")
    suspend fun fetchAttraction(
        @Query("offset") offset: Int = 0, // default offset = 0
        @Query("limit") limit: Int = 6, // default limit = 6
        @Query("dataset") dataset: String = "attractions",
        @Header("X-API-Key") apiKey: String,
        @Header("X-Content-Language") contentLanguage: String = "en"
    ): Response<AttractionResponse>

    @GET("content/attractions/v2/search")
    suspend fun fetchAttractionByUUID(
        @Query("searchType") searchType: String = "uuids",
        @Query("searchValues") uuid: String,
        @Header("X-API-Key") apiKey: String,
        @Header("X-Content-Language") contentLanguage: String = "en"
    ): Response<AttractionResponse>

    @GET("content/common/v2/search")
    suspend fun fetchAttractionByKeyword(
        @Query("keyword") keyword: String,
        @Query("dataset") dataset: String = "attractions", // 默认查询 attractions 数据集
        @Query("offset") offset: Int = 0, // 用于分页
        @Query("limit") limit: Int = 7, // 每次加载条目数量
        @Header("X-API-Key") apiKey: String,
        @Header("X-Content-Language") contentLanguage: String = "en"
    ): Response<AttractionResponse>

    @POST("/api/attractions") // ✅ 直接调用 JPA 默认 `save()` 方法
    suspend fun saveAttraction(@Body attraction: Attraction): Response<Void>

    //    @POST("api/attractions/save")
//    suspend fun sendTransformedAttractionData(
//        @Body attractions: List<Attraction>
//    ): Response<SaveResponse>
//
//    @POST("api/attractions/booking")
//    suspend fun sendAttractionBooking(
//        @Body booking: AttractionBooking
//    ): Response<BookingResponse>
    companion object {
        private const val BASE_URL = "https://api.stb.gov.sg" // 确保这个 URL 正确
        fun create(): AttractionApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // 解析 JSON
                .build()

            return retrofit.create(AttractionApiService::class.java)
        }


    }
}