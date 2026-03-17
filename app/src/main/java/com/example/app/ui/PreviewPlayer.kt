package com.example.app.ui

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.app.data.Song
import com.example.app.data.SpotifyConfig
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.UserNotAuthorizedException

class PreviewPlayer(private val activity: Activity) {
    private val appContext = activity.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var currentTrackUri: String? = null
    private var stopPlaybackRunnable: Runnable? = null

    fun playPreview(song: Song) {
        if (song.id.isBlank()) {
            showToast("This track cannot be played.")
            return
        }

        if (!SpotifyAppRemote.isSpotifyInstalled(appContext)) {
            showToast("Install the Spotify app on this device to play 30-second previews.")
            return
        }

        val trackUri = "spotify:track:${song.id}"
        connectIfNeeded { appRemote ->
            appRemote.playerApi.getPlayerState()
                .setResultCallback { playerState ->
                    val isSameTrack = playerState.track.uri == trackUri
                    if (isSameTrack && !playerState.isPaused) {
                        pauseInternal(appRemote, "Playback paused.")
                    } else {
                        startTrack(appRemote, trackUri)
                    }
                }
                .setErrorCallback { error ->
                    Log.w(TAG, "Unable to inspect current playback state", error)
                    startTrack(appRemote, trackUri)
                }
        }
    }

    fun pause() {
        cancelStopTimer()
        spotifyAppRemote
            ?.takeIf { it.isConnected }
            ?.playerApi
            ?.pause()
            ?.setResultCallback {
                currentTrackUri = null
            }
            ?.setErrorCallback { error ->
                Log.w(TAG, "Failed to pause Spotify playback", error)
            }
    }

    fun release() {
        cancelStopTimer()
        currentTrackUri = null
        spotifyAppRemote?.let(SpotifyAppRemote::disconnect)
        spotifyAppRemote = null
    }

    private fun connectIfNeeded(onConnected: (SpotifyAppRemote) -> Unit) {
        spotifyAppRemote
            ?.takeIf { it.isConnected }
            ?.let {
                onConnected(it)
                return
            }

        val connectionParams = ConnectionParams.Builder(SpotifyConfig.CLIENT_ID)
            .setRedirectUri(SpotifyConfig.REDIRECT_URI)
            .setAuthMethod(ConnectionParams.AuthMethod.APP_ID)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(activity, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                onConnected(appRemote)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e(TAG, "Spotify App Remote connection failed", throwable)
                val message = when (throwable) {
                    is CouldNotFindSpotifyApp ->
                        "Install the Spotify app on this device to play 30-second previews."
                    is UserNotAuthorizedException ->
                        "Log in again and approve Spotify playback control."
                    else ->
                        "Could not connect to Spotify playback."
                }
                showToast(message)
            }
        })
    }

    private fun startTrack(appRemote: SpotifyAppRemote, trackUri: String) {
        cancelStopTimer()
        appRemote.playerApi.play(trackUri)
            .setResultCallback {
                currentTrackUri = trackUri
                scheduleStop(appRemote)
                showToast("Playing 30 seconds in Spotify...")
            }
            .setErrorCallback { error ->
                Log.e(TAG, "Track playback failed", error)
                showToast("Could not start playback for this track.")
            }
    }

    private fun pauseInternal(appRemote: SpotifyAppRemote, toastMessage: String? = null) {
        cancelStopTimer()
        appRemote.playerApi.pause()
            .setResultCallback {
                currentTrackUri = null
                if (!toastMessage.isNullOrBlank()) {
                    showToast(toastMessage)
                }
            }
            .setErrorCallback { error ->
                Log.w(TAG, "Failed to pause Spotify playback", error)
            }
    }

    private fun scheduleStop(appRemote: SpotifyAppRemote) {
        val stopRunnable = Runnable {
            pauseInternal(appRemote, "30-second preview ended.")
        }
        stopPlaybackRunnable = stopRunnable
        mainHandler.postDelayed(stopRunnable, PREVIEW_DURATION_MS)
    }

    private fun cancelStopTimer() {
        stopPlaybackRunnable?.let(mainHandler::removeCallbacks)
        stopPlaybackRunnable = null
    }

    private fun showToast(message: String) {
        mainHandler.post {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "PreviewPlayer"
        private const val PREVIEW_DURATION_MS = 30_000L
    }
}
