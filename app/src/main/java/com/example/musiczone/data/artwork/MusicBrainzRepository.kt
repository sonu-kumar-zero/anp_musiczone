package com.example.musiczone.data.artwork

import android.util.Log
import org.json.JSONObject

class MusicBrainzRepository(
    private val api: MusicBrainzApi = MusicBrainzClient.api
) {

    suspend fun findReleaseGroupId(
        artist: String,
        album: String
    ): String? {
        if (artist.isBlank() || album.isBlank()) {
            return null
        }

        val query = "artist:\"$artist\" AND releasegroup:\"$album\""

        Log.d("MusicBrainz", "Searching: $query")

        return try {
            val response = api.searchReleaseGroups(
                query = query
            )

            val body = response.string()

            Log.d("MusicBrainz", "Response: $body")

            val json = JSONObject(body)

            val releaseGroups = json.optJSONArray(
                "release-groups"
            )

            if (releaseGroups == null) {
                Log.d("MusicBrainz", "No release-groups array")
                return null
            }

            if (releaseGroups.length() == 0) {
                Log.d("MusicBrainz", "No release groups found")
                return null
            }

            val first = releaseGroups.optJSONObject(0)

            val id = first
                ?.optString("id")
                ?.takeIf { it.isNotBlank() }

            Log.d("MusicBrainz", "Found MBID: $id")

            id
        } catch (exception: Exception) {
            Log.e(
                "MusicBrainz",
                "Lookup failed",
                exception
            )

            null
        }
    }
}