package com.tlapp.launchnothing.data.repository

import android.content.Context
import android.content.Intent
import com.tlapp.launchnothing.data.db.App
import com.tlapp.launchnothing.data.db.AppDao
import com.tlapp.launchnothing.data.models.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appDao: AppDao,
) {
    val apps = appDao.getApps()
        .map { dbApps ->
            dbApps.map { it.toAppInfo() }
        }

    suspend fun syncApps() {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = context.packageManager.queryIntentActivities(intent, 0)
        val apps = activities.map {
            App(
                packageName = it.activityInfo.packageName,
                label = it.loadLabel(context.packageManager).toString()
            )
        }
        appDao.upsertApps(apps)
    }
}

fun App.toAppInfo() = AppInfo(
    packageName = packageName,
    label = label
)
