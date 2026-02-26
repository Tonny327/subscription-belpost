package com.belpost.subscription.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkConfig {
    /**
     * Базовый URL продакшн‑бэкенда.
     * ВАЖНО: для Retrofit URL должен заканчиваться слешем.
     */
    const val BASE_URL: String = "https://subscription-belpost-production.up.railway.app/"
    //const val BASE_URL: String = "http://192.168.0.4:8082"
}

object RetrofitClient {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            // Для разработки включаем подробное логирование; при необходимости можно ослабить
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}

