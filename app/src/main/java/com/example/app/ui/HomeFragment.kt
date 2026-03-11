// FILE LOCATION: app/src/main/java/com/example/app/ui/HomeFragment.kt
package com.example.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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