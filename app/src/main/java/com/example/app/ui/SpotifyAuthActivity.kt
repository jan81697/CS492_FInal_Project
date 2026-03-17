package com.example.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse

/**
 * A dedicated activity to handle the Spotify Redirect URI.
 */
class SpotifyAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val intent = intent
        val response = AuthorizationClient.getResponse(RESULT_OK, intent)
        
        // You can handle the response here or pass it back to HomeFragment
        // For now, we'll just finish and let the calling fragment handle the result
        // if you use startActivityForResult
        finish()
    }
}