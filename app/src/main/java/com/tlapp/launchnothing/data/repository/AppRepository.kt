package com.tlapp.launchnothing.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.tlapp.launchnothing.data.db.App
import com.tlapp.launchnothing.data.db.AppDao
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

    val favoriteApps = appDao.getFavoriteApps()
        .map { dbApps ->
            dbApps.map { it.toAppInfo() }
        }

    suspend fun setFavorite(
        packageName: String,
        isFavorite: Boolean,
    ) {
        appDao.setFavorite(packageName, isFavorite)
    }

    suspend fun onAppUninstalled(packageName: String) {
        appDao.delete(packageName)
    }

    suspend fun onAppInstalled(packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
        val resolveInfo = context.packageManager.resolveActivity(launchIntent, 0) ?: return

        val isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val app = App(
            packageName = resolveInfo.activityInfo.packageName,
            label = resolveInfo.loadLabel(context.packageManager).toString(),
            isSystemApp = isSystemApp,
            isFavorite = false,
        )
        appDao.upsert(app)
    }
}
