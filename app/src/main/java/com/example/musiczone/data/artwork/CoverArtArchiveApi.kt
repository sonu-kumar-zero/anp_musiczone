package com.example.musiczone.data.artwork

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.ResponseBody

interface CoverArtArchiveApi {

    @GET("release-group/{mbid}/front-500")
    suspend fun getFrontArtwork(
        @Path("mbid") mbid: String
    ): Response<ResponseBody>
}