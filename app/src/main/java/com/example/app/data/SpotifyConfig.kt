package com.example.app.data

/**
 * Configuration for Spotify API authentication.
 */
object SpotifyConfig {
    const val CLIENT_ID = "6a89753b5b7e45d493f1f02b2e0ce107"
    const val REDIRECT_URI = "spotifystats://callback"
    const val REQUEST_CODE = 1337
    
    // Scopes define what data you want to access
    val SCOPES = arrayOf(
        "app-remote-control",
        "user-read-private",
        "user-read-email",
        "user-top-read",
        "user-read-recently-played"
    )
}
