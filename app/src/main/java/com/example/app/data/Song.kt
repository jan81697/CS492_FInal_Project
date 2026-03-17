package com.example.app.data

import java.io.Serializable

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val popularity: Int,
    val previewUrl: String? = null
) : Serializable