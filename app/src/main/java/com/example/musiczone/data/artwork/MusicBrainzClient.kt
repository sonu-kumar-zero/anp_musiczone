package com.example.musiczone.data.artwork

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object MusicBrainzClient {

    private const val BASE_URL = "https://musicbrainz.org/ws/2/"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .header(
                    "User-Agent",
                    "MusicZone/1.0"
                )
                .build()

            chain.proceed(request)
        }
        .build()

    val api: MusicBrainzApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .build()
        .create(MusicBrainzApi::class.java)
}