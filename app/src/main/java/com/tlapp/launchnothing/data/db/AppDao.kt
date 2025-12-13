package com.tlapp.launchnothing.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM apps ORDER BY label ASC")
    fun getApps(): Flow<List<App>>

    @Query("SELECT * FROM apps")
    suspend fun getAppsOnce(): List<App>

    @Upsert
    suspend fun upsertApps(apps: List<App>)

    @Upsert
    suspend fun upsert(app: App)

    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("DELETE FROM apps WHERE packageName IN (:packageNames)")
    suspend fun deleteApps(packageNames: List<String>)

    @Query("UPDATE apps SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun setFavorite(
        packageName: String,
        isFavorite: Boolean,
    )
}
