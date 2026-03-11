// FILE LOCATION: app/src/main/java/com/example/app/ui/ArtistAdapter.kt
package com.example.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.data.Artist

class ArtistAdapter(
    private val onArtistClick: (Artist) -> Unit
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    private var artists: List<Artist> = emptyList()

    fun updateArtists(newArtists: List<Artist>) {
        artists = newArtists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artists[position])
    }

    override fun getItemCount() = artists.size

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artistBtn: Button = itemView.findViewById(R.id.btn_artist)

        fun bind(artist: Artist) {
            artistBtn.text = artist.name
            artistBtn.setOnClickListener { onArtistClick(artist) }
        }
    }
}