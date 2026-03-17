package com.example.app.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.data.SpotifyConfig
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by activityViewModels()
    
    private val recentArtistsAdapter = ArtistAdapter { artist ->
        viewModel.updateArtistTimestamp(artist)
        val directions = HomeFragmentDirections.navigateToArtistDetail(artist)
        findNavController().navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvRecent = view.findViewById<RecyclerView>(R.id.rv_recent_artists)
        val tvLabel = view.findViewById<TextView>(R.id.tv_recent_searches_label)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val cvLogin = view.findViewById<View>(R.id.cv_login)
        val cvMainContent = view.findViewById<View>(R.id.cv_main_content)

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

        viewModel.accessToken.observe(viewLifecycleOwner) { token ->
            Log.d("SpotifyAuth", "HomeFragment: Access token observed: ${if (token != null) "PRESENT" else "NULL"}")
            if (token != null) {
                cvLogin.visibility = View.GONE
                cvMainContent.visibility = View.VISIBLE
            } else {
                cvLogin.visibility = View.VISIBLE
                cvMainContent.visibility = View.GONE
            }
        }

        btnLogin.setOnClickListener {
            Log.d("SpotifyAuth", "Login button clicked")
            val codeChallenge = viewModel.prepareForAuth()
            
            val builder = AuthorizationRequest.Builder(
                SpotifyConfig.CLIENT_ID,
                AuthorizationResponse.Type.CODE, 
                SpotifyConfig.REDIRECT_URI
            )
            builder.setScopes(SpotifyConfig.SCOPES)
            builder.setCustomParam("code_challenge_method", "S256")
            builder.setCustomParam("code_challenge", codeChallenge)
            
            val request = builder.build()
            AuthorizationClient.openLoginActivity(requireActivity(), SpotifyConfig.REQUEST_CODE, request)
        }

        // Using OFFICIAL Spotify Genre Seeds to prevent 404/400 errors
        view.findViewById<Button>(R.id.btn_happy).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Happy",
                genres = "pop" // 'happy' is not a valid genre seed
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_sad).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Sad",
                genres = "sad"
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_angry).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Angry",
                genres = "metal"
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_chill).setOnClickListener {
            val directions = HomeFragmentDirections.navigateToMoodResults(
                mood = "Chill",
                genres = "chill"
            )
            findNavController().navigate(directions)
        }

        view.findViewById<Button>(R.id.btn_search).setOnClickListener {
            findNavController().navigate(R.id.navigate_to_artist_search)
        }
    }
}