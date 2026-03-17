package com.example.app.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app.data.Artist
import com.example.app.data.Song
import com.example.app.data.SpotifyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _artistDetails = MutableLiveData<Artist?>(null)
    val artistDetails: LiveData<Artist?> = _artistDetails

    private val _topTracks = MutableLiveData<List<Song>>(emptyList())
    val topTracks: LiveData<List<Song>> = _topTracks

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    /**
     * Loads artist data using the new 2026 Single-Artist requirements.
     */
    fun loadArtistProfile(artistId: String, artistName: String, accessToken: String?) {
        if (accessToken.isNullOrBlank()) return

        _loading.value = true
        val auth = "Bearer $accessToken"

        viewModelScope.launch {
            try {
                val service = SpotifyClient.service
                
                // 1. Fetch Single Artist (Required for Dev Mode as of March 2026)
                val spotifyArtist = withContext(Dispatchers.IO) {
                    service.getArtist(auth, artistId)
                }
                
                _artistDetails.postValue(Artist(
                    id = spotifyArtist.id,
                    name = spotifyArtist.name,
                    popularity = spotifyArtist.popularity ?: -1,
                    followers = spotifyArtist.followers?.total ?: -1,
                    imageUrl = spotifyArtist.images?.firstOrNull()?.url,
                    spotifyUri = "spotify:artist:${spotifyArtist.id}",
                    externalUrl = "https://open.spotify.com/artist/${spotifyArtist.id}"
                ))

                // 2. Fetch Tracks via Search (Reliable fallback for 403 errors on top-tracks)
                val trackSearch = withContext(Dispatchers.IO) {
                    service.search(auth, "artist:\"$artistName\"", "track", 10)
                }
                
                val tracks = trackSearch.tracks?.items?.map { 
                    Song(it.id, it.name, artistName, it.popularity ?: 0, it.previewUrl)
                } ?: emptyList()
                
                _topTracks.postValue(tracks)
                
            } catch (e: Exception) {
                Log.e("SpotifyAPI", "Error loading profile with new 2026 rules", e)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}