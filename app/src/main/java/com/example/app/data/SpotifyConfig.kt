package com.example.app.data

/**
 * Configuration for Spotify API authentication.
 */
object SpotifyConfig {
    const val CLIENT_ID = "96992d9fbcae40c8a5f8c0ca4fe59405"
    const val REDIRECT_URI = "spotifystats://callback"
    const val REQUEST_CODE = 1337
    
    // Scopes define what data you want to access
    val SCOPES = arrayOf(
        "user-read-private",
        "user-read-email",
        "user-top-read",
        "user-read-recently-played"
    )
}