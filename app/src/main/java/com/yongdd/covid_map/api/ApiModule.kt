package com.yongdd.covid_map.api

import com.yongdd.covid_map.api.service.CenterService
import com.yongdd.covid_map.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    fun provideBaseUrl() = Constants.API_SERVER_BASE_URL

//    @Provides
//    fun provideApiKeyAuth() = Constants.API_KEY_AUTH

    @Provides
    fun provideInterceptor() =  Interceptor { chain: Interceptor.Chain ->
            synchronized(this) {
                val request = chain.request().newBuilder().addHeader("Authorization", "Infuser ${Constants.API_KEY_AUTH}").build()
                chain.proceed(request)
            }
        }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: Interceptor) : OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val builder = OkHttpClient.Builder()
        builder.interceptors().add(interceptor)
        builder.interceptors().add(logInterceptor)
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(10, TimeUnit.SECONDS)
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String) : Retrofit {
        return  Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideCenterService(retrofit: Retrofit) : CenterService {
        return  retrofit.create(CenterService::class.java)
    }

}