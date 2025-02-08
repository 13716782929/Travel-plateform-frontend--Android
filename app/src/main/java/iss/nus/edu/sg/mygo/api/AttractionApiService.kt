package iss.nus.edu.sg.mygo.api

/*
Author: Siti Alifah Binte Yahya
StudentID: A0295324B
Date: 7/2/2025
*/


import iss.nus.edu.sg.mygo.models.AccommodationImageResponse
import iss.nus.edu.sg.mygo.models.AttractionImageResponse
import iss.nus.edu.sg.mygo.models.AttractionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface AttractionApiService {

    @GET("/content/attractions/v2/search?searchType=keyword&searchValues=255&sort=name&sortOrder=asc HTTP/1.1")
    fun searchAttraction(
        @Header("X-API-KEY") apiKey: String, // secure API Key
        @Header("X-Content-Language") language: String = "en",
        @Query("searchType") searchType: String = "keyword",
        @Query("language") searchValues: String
    ): Call<AttractionResponse>


    @GET("/content/attractions/v2/search")
    fun searchAttractionByUUID(
        @Header("X-API-KEY") apiKey: String, //API Key
        @Header("X-Content-Language") language: String = "en",
        @Query("searchType") searchType: String = "uuids",
        @Query("language") searchValues: String
    ): Call<AttractionResponse>

    @GET("media/libraries/v2/details/{uuid}")
    fun getMediaDetails(
        @Header("X-API-Key") apiKey: String,  // API Key
        @Path("uuid") uuid: String
    ): Call<AttractionImageResponse>
}