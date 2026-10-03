package com.example.musiczone.data.artwork

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MusicBrainzApi {

    @Headers("Accept: application/json")
    @GET("release-group/")
    suspend fun searchReleaseGroups(
        @Query("query") query: String,
        @Query("fmt") format: String = "json",
        @Query("limit") limit: Int = 5
    ): ResponseBody
}