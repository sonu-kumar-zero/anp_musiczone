package com.example.musiczone.data.artwork

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object CoverArtArchiveClient {

    private const val BASE_URL =
        "https://coverartarchive.org/"

    private val httpClient = OkHttpClient.Builder()
        .build()

    val api: CoverArtArchiveApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .build()
        .create(CoverArtArchiveApi::class.java)
}