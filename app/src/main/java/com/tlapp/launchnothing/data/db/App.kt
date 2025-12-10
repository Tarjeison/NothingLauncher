package com.tlapp.launchnothing.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
data class App(
    @PrimaryKey val packageName: String,
    val label: String
)
