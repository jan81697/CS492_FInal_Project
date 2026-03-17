package com.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R

class MoodResultsFragment : Fragment(R.layout.fragment_mood_results) {
    private val viewModel: MoodResultsViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val args: MoodResultsFragmentArgs by navArgs()
    private lateinit var previewPlayer: PreviewPlayer
    
    private val songAdapter = SongAdapter { song ->
        previewPlayer.playPreview(song)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_mood_title).text =
            "Top Songs for ${args.mood}"

        previewPlayer = PreviewPlayer(requireActivity())

        val rv = view.findViewById<RecyclerView>(R.id.rv_songs)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.setHasFixedSize(true)
        rv.adapter = songAdapter

        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            songAdapter.updateSongs(songs)
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch recommendations using the stored access token
        homeViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            viewModel.loadSongsForMood(args.mood, args.genres, token)
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
