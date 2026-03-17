package com.example.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * A dedicated activity to handle the Spotify Redirect URI.
 * Manually parses the code to avoid SDK parsing issues.
 */
class SpotifyRedirectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val uri = intent?.data
        Log.d("SpotifyAuth", "RedirectActivity received URI: $uri")
        
        if (uri != null && uri.scheme == "spotifystats") {
            // Manually extract the code from the query parameter
            val code = uri.getQueryParameter("code")
            val error = uri.getQueryParameter("error")
            
            if (code != null) {
                Log.d("SpotifyAuth", "Successfully parsed code manually: ${code.take(5)}...")
                
                // Send the code to MainActivity
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.putExtra("auth_code", code)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(mainIntent)
            } else if (error != null) {
                Log.e("SpotifyAuth", "Redirect error: $error")
            }
        }
        
        finish()
    }
}