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

    suspend fun syncApps() {
        val freshResolveInfos = context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) },
            0
        )

        val staleApps = appDao.getAppsOnce()
        val staleAppsMap = staleApps.associateBy { it.packageName }

        val freshPackageNames = freshResolveInfos.map { it.activityInfo.packageName }.toSet()
        val stalePackageNames = staleAppsMap.keys

        val appsToDelete = stalePackageNames - freshPackageNames
        if (appsToDelete.isNotEmpty()) {
            appDao.deleteApps(appsToDelete.toList())
        }

        val appsToUpsert = freshResolveInfos.map { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            val existingApp = staleAppsMap[packageName]
            val isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            App(
                packageName = packageName,
                label = resolveInfo.loadLabel(context.packageManager).toString(),
                isSystemApp = isSystemApp,
                isFavorite = existingApp?.isFavorite == true
            )
        }

        if (appsToUpsert.isNotEmpty()) {
            appDao.upsertApps(appsToUpsert)
        }
    }
}
