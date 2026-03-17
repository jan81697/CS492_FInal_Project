package com.example.app.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val artistDao = AppDatabase.getDatabase(application).artistDao()
    private val sharedPrefs = application.getSharedPreferences("spotify_prefs", 0)
    
    val recentArtists: LiveData<List<Artist>> = artistDao.getRecentArtists().asLiveData()

    private val _accessToken = MutableLiveData<String?>(null)
    val accessToken: LiveData<String?> = _accessToken

    private val _userCountry = MutableLiveData<String>("US") // Default to US
    val userCountry: LiveData<String> = _userCountry

    fun updateArtistTimestamp(artist: Artist) {
        viewModelScope.launch {
            val updatedArtist = artist.copy(timestamp = System.currentTimeMillis())
            artistDao.insertArtist(updatedArtist)
        }
    }

    fun prepareForAuth(): String {
        val verifier = PKCEUtil.generateCodeVerifier()
        sharedPrefs.edit().putString("code_verifier", verifier).apply()
        return PKCEUtil.generateCodeChallenge(verifier)
    }

    fun onAuthCodeReceived(code: String) {
        val verifier = sharedPrefs.getString("code_verifier", null)
        if (verifier == null) return
        
        viewModelScope.launch {
            try {
                val service = SpotifyClient.service
                val response = withContext(Dispatchers.IO) {
                    service.getToken(
                        grantType = "authorization_code",
                        code = code,
                        redirectUri = SpotifyConfig.REDIRECT_URI,
                        clientId = SpotifyConfig.CLIENT_ID,
                        codeVerifier = verifier
                    )
                }
                
                _accessToken.value = response.accessToken
                // Immediately fetch user profile to get the correct country/market
                fetchUserProfile(response.accessToken)
                
            } catch (e: Exception) {
                Log.e("SpotifyAuth", "Token exchange failed", e)
            }
        }
    }

    private fun fetchUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    SpotifyClient.service.getCurrentUser("Bearer $token")
                }
                _userCountry.postValue(user.country)
                Log.d("SpotifyAPI", "User country detected: ${user.country}")
            } catch (e: Exception) {
                Log.e("SpotifyAPI", "Failed to fetch user profile", e)
            }
        }
    }

    fun onAuthTokenReceived(token: String?) {
        _accessToken.value = token
        if (token != null) fetchUserProfile(token)
    }
}