package com.example.app.ui

import android.app.SearchManager
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.app.R

class ArtistDetailFragment : Fragment(R.layout.fragment_artist_detail) {
    private val args: ArtistDetailFragmentArgs by navArgs()
    private val viewModel: ArtistDetailViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    
    private var mediaPlayer: MediaPlayer? = null

    private val songAdapter = SongAdapter { song ->
        playPreview(song.previewUrl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialArtist = args.artist
        
        val rvTracks = view.findViewById<RecyclerView>(R.id.rv_top_tracks)
        rvTracks.layoutManager = LinearLayoutManager(requireContext())
        rvTracks.adapter = songAdapter

        // Initial UI
        updateProfileUI(initialArtist.name, initialArtist.popularity, initialArtist.followers, initialArtist.imageUrl)

        viewModel.artistDetails.observe(viewLifecycleOwner) { fullArtist ->
            if (fullArtist != null) {
                updateProfileUI(fullArtist.name, fullArtist.popularity, fullArtist.followers, fullArtist.imageUrl)
                
                // Setup "Open in Spotify" button
                view.findViewById<Button>(R.id.btn_open_spotify).setOnClickListener {
                    val uri = Uri.parse(fullArtist.externalUrl ?: "https://open.spotify.com/artist/${fullArtist.id}")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }
        }

        viewModel.topTracks.observe(viewLifecycleOwner) { tracks ->
            songAdapter.updateSongs(tracks)
        }

        homeViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (!token.isNullOrBlank()) {
                viewModel.loadArtistProfile(initialArtist.id, initialArtist.name, token)
            }
        }
    }

    private fun playPreview(url: String?) {
        if (url.isNullOrBlank()) {
            Toast.makeText(requireContext(), "No preview available for this track", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { start() }
            }
            Toast.makeText(requireContext(), "Playing 30s preview...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error playing preview", e)
        }
    }

    private fun updateProfileUI(name: String, popularity: Int, followers: Int, imageUrl: String?) {
        val popText = if (popularity < 0) "N/A" else popularity.toString()
        val followersText = if (followers < 0) "N/A" else String.format("%,d", followers)

        view?.findViewById<TextView>(R.id.tv_artist_name)?.text = name
        view?.findViewById<TextView>(R.id.tv_artist_popularity)?.text = "Popularity: $popText"
        view?.findViewById<TextView>(R.id.tv_artist_followers)?.text = "Followers: $followersText"
        
        val imageView = view?.findViewById<ImageView>(R.id.iv_artist_image)
        imageView?.load(imageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.stat_notify_error)
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}