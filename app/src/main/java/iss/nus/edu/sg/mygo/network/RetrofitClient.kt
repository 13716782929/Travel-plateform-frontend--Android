package iss.nus.edu.sg.mygo.network

import iss.nus.edu.sg.mygo.api.AccommodationApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.stb.gov.sg/"  // API 基础 URL

    // 添加日志拦截器（可选）
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 构建 OkHttpClient
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // 日志拦截器
        .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
        .readTimeout(30, TimeUnit.SECONDS) // 读取超时
        .build()

    // 构建 Retrofit 实例
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // 解析 JSON
            .build()
    }

    // 提供 API 接口实例
    val accommodationApiService: AccommodationApiService by lazy {
        retrofit.create(AccommodationApiService::class.java)
    }
}
