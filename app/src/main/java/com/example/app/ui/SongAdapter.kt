// FILE LOCATION: app/src/main/java/com/example/app/ui/SongAdapter.kt
package com.example.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.data.Song

class SongAdapter(
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var songs: List<Song> = emptyList()

    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position], position + 1)
    }

    override fun getItemCount() = songs.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rankTV: TextView = itemView.findViewById(R.id.tv_rank)
        private val songBtn: Button = itemView.findViewById(R.id.btn_song)
        private val playIcon: ImageView = itemView.findViewById(R.id.iv_play_icon)

        fun bind(song: Song, rank: Int) {
            rankTV.text = "$rank."
            songBtn.text = song.title
            songBtn.setOnClickListener { onSongClick(song) }

            playIcon.alpha = if (song.id.isBlank()) 0.35f else 1f
            playIcon.setOnClickListener { onSongClick(song) }
        }
    }
}
