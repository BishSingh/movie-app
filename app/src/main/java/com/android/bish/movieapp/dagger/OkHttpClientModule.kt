package com.android.bish.movieapp.dagger

import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

@Module(includes = arrayOf(ContextModule::class))
class OkHttpClientModule {

    @Provides
    fun okHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, interceptor: Interceptor): OkHttpClient {
        return OkHttpClient()
                .newBuilder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(interceptor)
                .build()
    }

    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Timber.d(message) })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    fun interceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            val url = request.url().newBuilder()
                    .addQueryParameter("api_key", "044c62148de4444cce3137c9864cab33").build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }
    }
}
