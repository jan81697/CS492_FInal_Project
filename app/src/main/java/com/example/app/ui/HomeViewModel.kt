package com.example.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.app.data.AppDatabase
import com.example.app.data.Artist

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val artistDao = AppDatabase.getDatabase(application).artistDao()
    
    val recentArtists: LiveData<List<Artist>> = artistDao.getRecentArtists().asLiveData()
}