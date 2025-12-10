package com.tlapp.launchnothing.data.source

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tlapp.launchnothing.data.repository.AppRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PackageChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: AppRepository

    @Inject
    lateinit var externalScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED -> {
                externalScope.launch {
                    try {
                        repository.syncApps()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            else -> pendingResult.finish()
        }
    }
}
