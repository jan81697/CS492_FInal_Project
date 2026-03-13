package com.example.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Artist)

    @Query("SELECT * FROM artists ORDER BY timestamp DESC LIMIT 5")
    fun getRecentArtists(): Flow<List<Artist>>

    @Query("DELETE FROM artists")
    suspend fun deleteAllArtists()
}