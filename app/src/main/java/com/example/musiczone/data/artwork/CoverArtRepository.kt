package com.example.musiczone.data.artwork

class CoverArtRepository(
    private val api: CoverArtArchiveApi = CoverArtArchiveClient.api
) {

    suspend fun findFrontArtwork(
        releaseGroupId: String
    ): String? {
        if (releaseGroupId.isBlank()) {
            return null
        }

        return try {
            val response = api.getFrontArtwork(
                mbid = releaseGroupId
            )

            if (!response.isSuccessful) {
                return null
            }

            "https://coverartarchive.org/release-group/$releaseGroupId/front-500"
        } catch (_: Exception) {
            null
        }
    }
}