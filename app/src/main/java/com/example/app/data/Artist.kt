package com.example.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey
    val id: String,
    val name: String,
    val popularity: Int,
    val followers: Int,
    val imageUrl: String? = null,
    val spotifyUri: String? = null,
    val externalUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable