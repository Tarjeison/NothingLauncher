package com.tlapp.freelauncher

import android.app.Application
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppInfo(
    val label: CharSequence,
    val packageName: CharSequence,
    val icon: Drawable
)

class AllAppsViewModel(application: Application) : AndroidViewModel(application) {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps = _apps.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        val context = getApplication<Application>().applicationContext
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val appInfos = packageManager.queryIntentActivities(intent, 0).map {
            AppInfo(
                label = it.loadLabel(packageManager),
                packageName = it.activityInfo.packageName,
                icon = it.loadIcon(packageManager)
            )
        }
        _apps.value = appInfos
    }
}
