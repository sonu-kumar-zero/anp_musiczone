package com.example.musiczone.data.artwork

import android.util.Log

class ArtworkRepository(
    private val artworkCache: ArtworkCache,
    private val musicBrainzRepository: MusicBrainzRepository = MusicBrainzRepository(),
    private val coverArtRepository: CoverArtRepository = CoverArtRepository()
) {

    suspend fun findArtwork(
        artist: String, album: String
    ): String? {


        if (artist.isBlank() || album.isBlank() || artist == "<unknown>" || album == "<unknown>") {
            Log.d(
                "ArtworkRepository", "Insufficient metadata"
            )
            return null
        }

        artworkCache.getArtworkUrl(
            artist = artist, album = album
        )?.let { cachedUrl ->

            return cachedUrl
        }


        val releaseGroupId = musicBrainzRepository.findReleaseGroupId(
            artist = artist, album = album
        )

        if (releaseGroupId == null) {
            return null
        }

        val artworkUrl = coverArtRepository.findFrontArtwork(
            releaseGroupId = releaseGroupId
        )

        if (artworkUrl == null) {
            return null
        }

        artworkCache.saveArtworkUrl(
            artist = artist, album = album, artworkUrl = artworkUrl
        )

        return artworkUrl
    }
}