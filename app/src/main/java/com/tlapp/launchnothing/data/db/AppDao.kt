package com.tlapp.launchnothing.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM apps ORDER BY label ASC")
    fun getApps(): Flow<List<App>>

    @Upsert
    suspend fun upsertApps(apps: List<App>)
}
