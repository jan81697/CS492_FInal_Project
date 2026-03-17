package com.example.app.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app.data.Song
import com.example.app.data.SpotifyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodResultsViewModel(application: Application) : AndroidViewModel(application) {

    private val _songs = MutableLiveData<List<Song>>(emptyList())
    val songs: LiveData<List<Song>> = _songs

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadSongsForMood(mood: String, genres: String, accessToken: String?) {
        if (accessToken.isNullOrBlank()) {
            _error.value = "Not logged in"
            return
        }

        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val service = SpotifyClient.service
                
                // Clean the access token to ensure no leading/trailing whitespace
                val cleanToken = accessToken.trim()
                val authHeader = "Bearer $cleanToken"
                
                // Simpler search query to test
                val query = "genre:$genres"
                Log.d("SpotifyAPI", "Requesting: $query with token length: ${cleanToken.length}")
                
                val response = withContext(Dispatchers.IO) {
                    service.search(
                        authHeader = authHeader,
                        query = query,
                        type = "track",
                        limit = 10
                    )
                }

                val songsList = response.tracks?.items
                    ?.map { spotifyTrack ->
                        Song(
                            id = spotifyTrack.id,
                            title = spotifyTrack.name,
                            artist = spotifyTrack.artists.firstOrNull()?.name ?: "Unknown Artist",
                            popularity = spotifyTrack.popularity ?: 0,
                            previewUrl = spotifyTrack.previewUrl
                        )
                    }
                    ?: emptyList()
                
                _songs.value = songsList
                
                if (songsList.isEmpty()) {
                    _error.value = "No results found."
                }
                
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("SpotifyAPI", "HTTP Error ${e.code()}: $errorBody")
                _error.value = "Spotify Error ${e.code()}: Check Logcat"
            } catch (e: Exception) {
                Log.e("SpotifyAPI", "Connection Error", e)
                _error.value = "Network Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
