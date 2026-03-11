// FILE LOCATION: app/src/main/java/com/example/app/ui/ArtistSearchViewModel.kt
package com.example.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.data.Artist

class ArtistSearchViewModel : ViewModel() {

    private val _artists = MutableLiveData<List<Artist>>(emptyList())
    val artists: LiveData<List<Artist>> = _artists

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // Stub data — replace with real Spotify /search?type=artist API call
    private val stubArtists = listOf(
        Artist("1", "Kendrick Lamar", 98, 25000000),
        Artist("2", "Kendrick Lamar Jr.", 70, 500000),
        Artist("3", "Taylor Swift", 100, 90000000),
        Artist("4", "Drake", 97, 80000000),
        Artist("5", "Billie Eilish", 95, 60000000),
        Artist("6", "The Weeknd", 96, 75000000)
    )

    fun searchArtists(query: String) {
        if (query.isBlank()) {
            _artists.value = emptyList()
            return
        }
        _loading.value = true

        // --- STUB DATA — replace with real Spotify search API call ---
        _artists.value = stubArtists.filter {
            it.name.contains(query, ignoreCase = true)
        }
        // --- END STUB ---

        _loading.value = false
    }
}