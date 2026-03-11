// FILE LOCATION: app/src/main/java/com/example/app/ui/MoodResultsFragment.kt
package com.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R

class MoodResultsFragment : Fragment(R.layout.fragment_mood_results) {
    private val viewModel: MoodResultsViewModel by viewModels()
    private val args: MoodResultsFragmentArgs by navArgs()
    private val songAdapter = SongAdapter { song ->
        // TODO: handle song click — navigate to song detail screen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_mood_title).text =
            "Top Songs for ${args.mood}"

        val rv = view.findViewById<RecyclerView>(R.id.rv_songs)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.setHasFixedSize(true)
        rv.adapter = songAdapter

        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            songAdapter.updateSongs(songs)
        }

        viewModel.loadSongsForMood(args.mood, args.genres)

        view.findViewById<Button>(R.id.btn_back_home).setOnClickListener {
            findNavController().navigate(R.id.home)
        }
    }
}