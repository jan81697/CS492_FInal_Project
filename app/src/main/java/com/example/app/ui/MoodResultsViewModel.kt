// FILE LOCATION: app/src/main/java/com/example/app/ui/MoodResultsViewModel.kt
package com.example.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.data.Song

class MoodResultsViewModel : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>(emptyList())
    val songs: LiveData<List<Song>> = _songs

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    /**
     * Loads top songs for the given mood and genres.
     * Replace the stub block below with a real Spotify API call when ready.
     *
     * @param mood    Display name e.g. "Happy"
     * @param genres  Comma-separated Spotify genre seeds e.g. "pop,dance,happy"
     */
    fun loadSongsForMood(mood: String, genres: String) {
        _loading.value = true

        // --- STUB DATA — replace with real Spotify recommendations API call ---
        _songs.value = listOf(
            Song("1", "Song 1 - $mood", "Artist A", 95),
            Song("2", "Song 2 - $mood", "Artist B", 90),
            Song("3", "Song 3 - $mood", "Artist C", 85),
            Song("4", "Song 4 - $mood", "Artist D", 80),
            Song("5", "Song 5 - $mood", "Artist E", 75)
        )
        // --- END STUB ---

        _loading.value = false
    }
}