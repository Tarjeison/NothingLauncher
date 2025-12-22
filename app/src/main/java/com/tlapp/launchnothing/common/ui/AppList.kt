package com.tlapp.launchnothing.common.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tlapp.launchnothing.data.models.AppInfo

@Composable
fun AppList(
    apps: List<AppInfo>,
    expandedAppPackageName: String?,
    onAppLongPressed: (String) -> Unit,
    onUninstallAppClicked: (String) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onDismissMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(apps) { app ->
            AppListItem(
                app = app,
                isExpanded = expandedAppPackageName == app.packageName,
                onAppClick = {
                    if (expandedAppPackageName == app.packageName) {
                        onDismissMenu()
                    } else {
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(app.packageName)
                        context.startActivity(intent)
                    }
                },
                onAppLongClick = { onAppLongPressed(app.packageName) },
                onToggleFavorite = { onToggleFavorite(app.packageName, !app.isFavorite) },
                onUninstallClick = { onUninstallAppClicked(app.packageName) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
