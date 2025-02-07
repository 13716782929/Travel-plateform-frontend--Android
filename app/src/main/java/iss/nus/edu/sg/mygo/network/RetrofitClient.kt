package iss.nus.edu.sg.mygo.network


import iss.nus.edu.sg.mygo.api.service.AccommodationApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.stb.gov.sg/"  // API 基础 URL
    private const val BACKEND_URL = "http://10.0.2.2:8080"

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

    private val mediaRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val accommodationApiService: AccommodationApiService by lazy {
        retrofit.create(AccommodationApiService::class.java)
    }


}
