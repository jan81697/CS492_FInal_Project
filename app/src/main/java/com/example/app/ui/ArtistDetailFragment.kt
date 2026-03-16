package com.example.app.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.app.R

class ArtistDetailFragment : Fragment(R.layout.fragment_artist_detail) {
    private val args: ArtistDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val artist = args.artist

        view.findViewById<TextView>(R.id.tv_artist_name).text = artist.name
        view.findViewById<TextView>(R.id.tv_artist_popularity).text = "Popularity: ${artist.popularity}"
        view.findViewById<TextView>(R.id.tv_artist_followers).text = "Followers: ${artist.followers}"

        view.findViewById<ImageButton>(R.id.btn_web_search).setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, artist.name)
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }
    }
}