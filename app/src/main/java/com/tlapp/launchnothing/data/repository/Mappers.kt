package com.tlapp.launchnothing.data.repository

import com.tlapp.launchnothing.data.db.App
import com.tlapp.launchnothing.data.models.AppInfo

fun App.toAppInfo(): AppInfo = AppInfo(
    packageName = packageName,
    label = label,
    isSystemApp = isSystemApp,
)
