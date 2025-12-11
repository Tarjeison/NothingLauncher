package com.tlapp.launchnothing

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.tlapp.launchnothing.data.repository.AppRepository
import com.tlapp.launchnothing.data.source.PackageChangeReceiver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltAndroidApp
class LaunchNothingApplication : Application() {

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var externalScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        registerReceiver()
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
