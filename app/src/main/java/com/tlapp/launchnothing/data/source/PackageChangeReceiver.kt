package com.tlapp.launchnothing.data.source

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tlapp.launchnothing.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PackageChangeReceiver(
    private val repository: AppRepository,
    private val externalScope: CoroutineScope,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()

        externalScope.launch {
            try {
                val packageName = intent?.data?.schemeSpecificPart ?: return@launch

                when (intent.action) {
                    Intent.ACTION_PACKAGE_ADDED,
                    Intent.ACTION_PACKAGE_REPLACED -> repository.onAppInstalled(packageName)
                    Intent.ACTION_PACKAGE_REMOVED -> repository.onAppUninstalled(packageName)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
