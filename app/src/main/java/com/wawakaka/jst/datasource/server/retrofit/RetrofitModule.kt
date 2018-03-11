package com.wawakaka.jst.datasource.server.retrofit

import com.google.gson.Gson
import com.wawakaka.jst.BuildConfig
import com.wawakaka.jst.datasource.server.model.ServerApi
import com.wawakaka.jst.datasource.server.utils.enableTlsOnPreLollipop
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by wawakaka on 7/25/2017.
 */
@Module
class RetrofitModule {
    @Singleton
    @Provides
    fun provideServerApi(@Named("server_retrofit") retrofit: Retrofit): ServerApi {
        return retrofit.create(ServerApi::class.java)
    }

    @Singleton
    @Provides
    @Named("server_retrofit")
    fun provideServerRetrofit(okHttpClient: OkHttpClient,
                              @Named("base_url") baseUrl: String,
                              callAdapterFactory: CallAdapter.Factory,
                              @Named("gson_converter") gsonConverter: Converter.Factory,
                              @Named("any_on_empty_converter") anyOnEmptyConverter: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(anyOnEmptyConverter)
            .addConverterFactory(gsonConverter)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(@Named("header_interceptor") headerInterceptor: Interceptor,
                            @Named("http_logging_interceptor") loggingInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .enableTlsOnPreLollipop(TlsVersion.TLS_1_2.javaName())
            .build()
    }

    @Singleton
    @Provides
    @Named("header_interceptor")
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val headerInterceptedRequest = request.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .method(request.method(), request.body())
                .build()

            chain.proceed(headerInterceptedRequest)
        }
    }

    @Singleton
    @Provides
    @Named("http_logging_interceptor")
    fun provideHttpLoggingInterceptor(@Named("log_enabled") logEnabled: Boolean): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = if (logEnabled) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return httpLoggingInterceptor
    }

    @Provides
    @Named("base_url")
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    @Singleton
    @Provides
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
    }

    @Singleton
    @Provides
    @Named("gson_converter")
    fun provideConverterFactory(@Named("lower_case_with_underscores_gson") gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    @Named("any_on_empty_converter")
    fun provideAnyOnEmptyConverter(): Converter.Factory {
        return object : Converter.Factory() {
            override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, Any> {
                val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, type, annotations)
                return Converter { body ->
                    if (body.contentLength() == 0L) return@Converter null
                    delegate.convert(body)
                }
            }
        }
    }
}