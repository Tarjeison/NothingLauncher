package com.tlapp.launchnothing.data.repository

import android.content.Context
import android.content.Intent
import com.tlapp.launchnothing.data.db.App
import com.tlapp.launchnothing.data.db.AppDao
import com.tlapp.launchnothing.data.repository.toAppInfo
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

    suspend fun onAppUninstalled(packageName: String) {
        appDao.delete(packageName)
    }

    suspend fun onAppInstalled(packageName: String) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = context.packageManager.queryIntentActivities(intent, 0)
        val installedApp = activities.find { it.activityInfo.packageName == packageName } ?: return

        val app = App(
            packageName = installedApp.activityInfo.packageName,
            label = installedApp.loadLabel(context.packageManager).toString()
        )
        appDao.upsert(app)
    }

    suspend fun syncApps() {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val freshApps = context.packageManager.queryIntentActivities(intent, 0)
            .map {
                App(
                    packageName = it.activityInfo.packageName,
                    label = it.loadLabel(context.packageManager).toString()
                )
            }

        val staleApps = appDao.getAppsOnce()

        val freshPackageNames = freshApps.map { it.packageName }.toSet()
        val stalePackageNames = staleApps.map { it.packageName }.toSet()

        val appsToDelete = stalePackageNames - freshPackageNames

        if (appsToDelete.isNotEmpty()) {
            appDao.deleteApps(appsToDelete.toList())
        }

        appDao.upsertApps(freshApps)
    }
}
