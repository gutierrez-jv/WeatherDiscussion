package com.example.weatherdiscussion.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.bumptech.glide.manager.ConnectivityMonitor
import com.example.weatherdiscussion.BuildConfig
import com.example.weatherdiscussion.data.DefaultWeatherRepository
import com.example.weatherdiscussion.data.WeatherRepository
import com.example.weatherdiscussion.data.remote.APIKeyInterceptor
import com.example.weatherdiscussion.data.remote.OfflineInterceptor
import com.example.weatherdiscussion.data.remote.WeatherAPI
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class AppContainer(context: Context){
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val baseOKtHttp: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val apiOkHTTP: OkHttpClient = baseOKtHttp.newBuilder()
        .cache(Cache(File(context.cacheDir,"http"), 10L * 1024 * 1024))
        .addInterceptor(APIKeyInterceptor(BuildConfig.OPENWEATHER_API_KEY))
        .addInterceptor(OfflineInterceptor({context.isOnline()}))
        .addInterceptor(logging)
        .build()

    val api: WeatherAPI = Retrofit.Builder()
        .baseUrl(WeatherAPI.BASE_URL)
        .client(apiOkHTTP)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(WeatherAPI::class.java)

    val weatherRepository: WeatherRepository = DefaultWeatherRepository(api, json)

    private fun Context.isOnline() : Boolean{
        val cm = getSystemService(ConnectivityManager::class.java) ?: return true
        val networkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

