package com.tlapp.launchnothing.feature.all_apps

import com.tlapp.launchnothing.data.models.AppInfo

data class AllAppsUiState(
    val apps: List<AppInfo> = emptyList(),
    val expandedAppPackageName: String? = null,
)
