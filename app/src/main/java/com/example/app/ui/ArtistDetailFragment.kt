package com.example.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var previewPlayer: PreviewPlayer

    private val songAdapter = SongAdapter { song ->
        previewPlayer.playPreview(song)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialArtist = args.artist
        previewPlayer = PreviewPlayer(requireActivity())
        
        val rvTracks = view.findViewById<RecyclerView>(R.id.rv_top_tracks)
        rvTracks.layoutManager = LinearLayoutManager(requireContext())
        rvTracks.adapter = songAdapter

        // Initial UI
        updateProfileUI(initialArtist.name, initialArtist.imageUrl)

        viewModel.artistDetails.observe(viewLifecycleOwner) { fullArtist ->
            if (fullArtist != null) {
                updateProfileUI(fullArtist.name, fullArtist.imageUrl)
                
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

    private fun updateProfileUI(name: String, imageUrl: String?) {
        view?.findViewById<TextView>(R.id.tv_artist_name)?.text = name

        val imageView = view?.findViewById<ImageView>(R.id.iv_artist_image)
        imageView?.load(imageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.stat_notify_error)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::previewPlayer.isInitialized) {
            previewPlayer.pause()
        }
    }

    override fun onDestroyView() {
        if (::previewPlayer.isInitialized) {
            previewPlayer.release()
        }
        super.onDestroyView()
    }
}
