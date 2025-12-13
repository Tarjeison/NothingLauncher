package com.tlapp.launchnothing.data.models

data class AppInfo(
    val packageName: String,
    val label: String,
    val isSystemApp: Boolean,
    val isFavorite: Boolean,
)
