package com.example.app.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app.data.AppDatabase
import com.example.app.data.Artist
import com.example.app.data.SpotifyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtistSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val artistDao = AppDatabase.getDatabase(application).artistDao()

    private val _artists = MutableLiveData<List<Artist>>(emptyList())
    val artists: LiveData<List<Artist>> = _artists

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    /**
     * Searches for artists using the real Spotify Search API via the unified SpotifyClient.
     */
    fun searchArtists(query: String, accessToken: String?) {
        if (query.isBlank()) {
            _artists.value = emptyList()
            return
        }
        
        if (accessToken == null) {
            _error.value = "Not logged in"
            return
        }

        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val service = SpotifyClient.service
                
                val response = withContext(Dispatchers.IO) {
                    service.search(
                        authHeader = "Bearer $accessToken",
                        query = query,
                        type = "artist",
                        limit = 10 
                    )
                }

                val artistList = response.artists?.items?.map { spotifyArtist ->
                    Artist(
                        id = spotifyArtist.id,
                        name = spotifyArtist.name,
                        popularity = spotifyArtist.popularity ?: 0,
                        followers = spotifyArtist.followers?.total ?: 0
                    )
                } ?: emptyList()
                
                _artists.value = artistList
                
                if (artistList.isEmpty()) {
                    Log.d("SpotifyAPI", "No artists found for query: $query")
                }
                
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("SpotifyAPI", "Search HTTP Error ${e.code()}: $errorBody")
                _error.value = "Search Error: ${e.code()}"
            } catch (e: Exception) {
                Log.e("SpotifyAPI", "Search Connection Error", e)
                _error.value = "Failed to search: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveArtist(artist: Artist) {
        viewModelScope.launch {
            val artistWithCurrentTimestamp = artist.copy(timestamp = System.currentTimeMillis())
            artistDao.insertArtist(artistWithCurrentTimestamp)
        }
    }
}
