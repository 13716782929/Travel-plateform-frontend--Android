package iss.nus.edu.sg.mygo.api

import iss.nus.edu.sg.mygo.models.AccommodationImageResponse
import iss.nus.edu.sg.mygo.models.AccommodationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface AccommodationApiService {

    @GET("content/accommodation/v2/search")
    fun searchAccommodation(
        @Header("X-API-Key") apiKey: String,  // API Key 作为请求头
        @Header("X-Content-Language") language: String = "en", // 默认英语
        @Query("searchType") searchType: String = "keyword", // 搜索类型
        @Query("searchValues") searchValues: String // 搜索关键字
    ): Call<AccommodationResponse>

    @GET("media/libraries/v2/details/{uuid}")
    fun getMediaDetails(
        @Header("X-API-Key") apiKey: String,  // API Key 作为请求头
        @Path("uuid") uuid: String
    ): Call<AccommodationImageResponse>


//    @GET("content/common/v2/search")
//    fun allAccommodation(
//        @Header("X-API-Key") apiKey: String,  // API Key 作为请求头
//        @Header("X-Content-Language") language: String = "en", // 默认英语
//        @Query("dataset") dataset: String = "accommodation", // 搜索类型
//    ): Call<AccommodationResponse>

}
