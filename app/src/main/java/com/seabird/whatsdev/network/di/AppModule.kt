package com.seabird.whatsdev.network.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.seabird.whatsdev.BuildConfig
import com.seabird.whatsdev.network.api.ApiHelper
import com.seabird.whatsdev.network.api.ApiHelperImpl
import com.seabird.whatsdev.network.api.AuthApiService
import com.seabird.whatsdev.network.api.OpenApiService
import com.seabird.whatsdev.utils.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    fun provideBaseUrl() = AppConstants.BASE_URL

    @Provides
    @Singleton
    fun providesOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun providesRequestTokenInterceptor(): RequestTokenInterceptor = RequestTokenInterceptor()

    @Provides
    fun providesNetworkInterceptor(): StethoInterceptor = StethoInterceptor()

    @Provides
    fun providesTokenAuthenticator(): TokenAuthenticator = TokenAuthenticator()

    @Singleton
    @Provides
    @Named("open")
    fun provideOkHttpClientWithoutAuthentication(okhttpBuilder: OkHttpClient.Builder,
                                                 stethoInterceptor: StethoInterceptor,
                                                 loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        if (BuildConfig.DEBUG)
            okhttpBuilder.addInterceptor(loggingInterceptor)

        return okhttpBuilder.addNetworkInterceptor(stethoInterceptor).connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .followRedirects(false).build()
    }

    @Singleton
    @Provides
    @Named("auth")
    fun providesOkhttpClientWithAuthentication(okhttpBuilder: OkHttpClient.Builder,
                                               stethoInterceptor: StethoInterceptor,
                                               tokenAuthenticator: TokenAuthenticator,
                                               requestTokenInterceptor: RequestTokenInterceptor
    ): OkHttpClient {

        return okhttpBuilder.addNetworkInterceptor(stethoInterceptor)
            .addInterceptor(requestTokenInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .followRedirects(false).build()
    }

    @Singleton
    @Provides
    @Named("open")
    fun providesRetrofitWithoutAuthentication(@Named("open") okHttpClient: OkHttpClient, BASE_URL:String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    @Singleton
    @Provides
    @Named("auth")
    fun providesRetrofitWithAuthentication(@Named("auth") okHttpClient: OkHttpClient, BASE_URL:String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOpenApiService(@Named("open") retrofit: Retrofit) = retrofit.create(OpenApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("auth") retrofit: Retrofit) = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

}