package com.example.registernewuser.api

/*
Author: YaoYiyang & Siti Alifah Binte Yahya
StudentID:  E1349011 & A0295324B
Date: 7 Feb 2025
*/


import iss.nus.edu.sg.mygo.api.AttractionApiService
import iss.nus.edu.sg.mygo.api.AttractionMediaImageService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.stb.gov.sg/"  // API  URL
    private const val BACKEND_URL = "http://10.0.2.2:8080" //backend

    // 添加日志拦截器（可选）
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) //
        .connectTimeout(30, TimeUnit.SECONDS) //
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // 解析 JSON
            .build()
    }
    //retrofit for attraction API
    private val apiService: AttractionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AttractionApiService::class.java)
    }


    val attractionApiService: AttractionApiService by lazy {
        retrofit.create(AttractionApiService::class.java)
    }

}