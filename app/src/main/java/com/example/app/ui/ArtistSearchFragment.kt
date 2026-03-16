package com.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.example.app.R

class ArtistSearchFragment : Fragment(R.layout.fragment_artist_search) {
    private val viewModel: ArtistSearchViewModel by viewModels()
    private val artistAdapter = ArtistAdapter { artist ->
        viewModel.saveArtist(artist)
        val directions = ArtistSearchFragmentDirections.navigateToArtistDetail(artist)
        findNavController().navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rv_artists)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.setHasFixedSize(true)
        rv.adapter = artistAdapter

        viewModel.artists.observe(viewLifecycleOwner) { artists ->
            artistAdapter.updateArtists(artists)
        }

        view.findViewById<TextInputEditText>(R.id.et_search).addTextChangedListener { text ->
            viewModel.searchArtists(text.toString())
        }

        view.findViewById<Button>(R.id.btn_back_home).setOnClickListener {
            findNavController().navigate(R.id.home)
        }
    }
}