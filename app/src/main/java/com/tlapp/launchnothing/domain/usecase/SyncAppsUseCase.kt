package com.tlapp.launchnothing.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.tlapp.launchnothing.data.db.App
import com.tlapp.launchnothing.data.db.AppDao
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SyncAppsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao,
) {
    suspend operator fun invoke() {
        val staleApps = appDao.getAppsOnce()
        val isFirstSync = staleApps.isEmpty()

        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val freshResolveInfos = context.packageManager.queryIntentActivities(intent, 0)

        val defaultFavoritePackages = if (isFirstSync) {
            getDefaultFavoritePackages()
        } else {
            emptySet()
        }

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
                isFavorite = existingApp?.isFavorite ?: (packageName in defaultFavoritePackages)
            )
        }

        if (appsToUpsert.isNotEmpty()) {
            appDao.upsertApps(appsToUpsert)
        }
    }

    private fun getDefaultFavoritePackages(): Set<String> {
        val packageManager = context.packageManager
        val defaultPackages = mutableSetOf<String>()

        // Get default dialer
        val dialIntent = Intent(Intent.ACTION_DIAL)
        packageManager.resolveActivity(dialIntent, 0)?.activityInfo?.packageName?.let { defaultPackages.add(it) }

        // Get default messaging app
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.data = android.net.Uri.parse("smsto:")
        packageManager.resolveActivity(smsIntent, 0)?.activityInfo?.packageName?.let { defaultPackages.add(it) }

        // Get default browser
        val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://"))
        packageManager.resolveActivity(browserIntent, 0)?.activityInfo?.packageName?.let { defaultPackages.add(it) }

        // Get default calendar
        val calendarIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CALENDAR) }
        packageManager.resolveActivity(calendarIntent, 0)?.activityInfo?.packageName?.let { defaultPackages.add(it) }

        // Get default camera
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        packageManager.resolveActivity(cameraIntent, 0)?.activityInfo?.packageName?.let { defaultPackages.add(it) }

        return defaultPackages
    }
}
