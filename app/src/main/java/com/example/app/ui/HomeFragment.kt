package com.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private val recentArtistsAdapter = ArtistAdapter { artist ->
        viewModel.updateArtistTimestamp(artist)
        val directions = HomeFragmentDirections.navigateToArtistDetail(artist)
        findNavController().navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvRecent = view.findViewById<RecyclerView>(R.id.rv_recent_artists)
        val tvLabel = view.findViewById<TextView>(R.id.tv_recent_searches_label)

        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.adapter = recentArtistsAdapter

        viewModel.recentArtists.observe(viewLifecycleOwner) { artists ->
            if (artists.isNullOrEmpty()) {
                rvRecent.visibility = View.GONE
                tvLabel.visibility = View.GONE
            } else {
                rvRecent.visibility = View.VISIBLE
                tvLabel.visibility = View.VISIBLE
                recentArtistsAdapter.updateArtists(artists)
            }
        }

        view.findViewById<Button>(R.id.btn_happy).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Happy",
                genres = "pop,dance,happy,summer,party"
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_sad).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Sad",
                genres = "sad,rainy-day,acoustic,singer-songwriter,emo"
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_angry).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Angry",
                genres = "metal,hard-rock,punk,hardcore,rage"
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_chill).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Chill",
                genres = "chill,lo-fi,ambient,sleep,study"
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_search).setOnClickListener {
            findNavController().navigate(R.id.navigate_to_artist_search)
        }
    }
}