package com.tlapp.launchnothing

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.tlapp.launchnothing.data.repository.AppRepository
import com.tlapp.launchnothing.data.source.PackageChangeReceiver
import com.tlapp.launchnothing.domain.usecase.SyncAppsUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LaunchNothingApplication : Application() {

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var externalScope: CoroutineScope

    @Inject
    lateinit var syncAppsUseCase: SyncAppsUseCase

    override fun onCreate() {
        super.onCreate()
        registerReceiver()

        externalScope.launch {
            syncAppsUseCase()
        }
    }

    private fun registerReceiver() {
        val receiver = PackageChangeReceiver(
            repository = appRepository,
            externalScope = externalScope,
        )
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(receiver, intentFilter)
    }
}
