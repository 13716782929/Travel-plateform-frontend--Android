package iss.nus.edu.sg.mygo.api.service

import iss.nus.edu.sg.mygo.api.models.AccommodationResponse
import iss.nus.edu.sg.mygo.models.Attraction
import iss.nus.edu.sg.mygo.models.Hotel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @ClassName AccommodationApiService
 * @Description
 * @Author YAO YIYANG
 * @StudentID A0294873L
 * @Date 2025/2/11
 * @Version 1.0
 */

interface AccommodationApiService {

    @GET("content/accommodation/v2/search")
    fun searchAccommodation(
        @Header("X-API-Key") apiKey: String,  // API Key 作为请求头
        @Header("X-Content-Language") language: String = "en", // 默认英语
        @Query("searchType") searchType: String = "keyword", // 搜索类型
        @Query("searchValues") searchValues: String // 搜索关键字
    ): Call<AccommodationResponse>

    @GET("content/accommodation/v2/search")
    fun searchAccommodationByUUID(
        @Header("X-API-Key") apiKey: String,  // API Key 作为请求头
        @Header("X-Content-Language") language: String = "en", // 默认英语
        @Query("searchType") searchType: String = "uuids", // 搜索类型
        @Query("searchValues") searchValues: String // 搜索关键字
    ): Call<AccommodationResponse>



//    @GET("content/common/v2/search")
//    fun allAccommodation(
//        @Header("X-API-Key") apiKey: String,  // API Key 作为请求头
//        @Header("X-Content-Language") language: String = "en", // 默认英语
//        @Query("dataset") dataset: String = "accommodation", // 搜索类型
//    ): Call<AccommodationResponse>


}
