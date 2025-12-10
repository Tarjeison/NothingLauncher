package com.tlapp.launchnothing

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlapp.launchnothing.data.AppInfo
import com.tlapp.launchnothing.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiAppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable
)

@HiltViewModel
class AllAppsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appRepository: AppRepository
) : ViewModel() {

    private val packageManager: PackageManager = context.packageManager

    val apps = appRepository.apps
        .map { domainApps ->
            domainApps.map { appInfo ->
                val icon = packageManager.getApplicationIcon(appInfo.packageName)
                UiAppInfo(appInfo.label, appInfo.packageName, icon)
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            appRepository.syncApps()
        }
    }
}
