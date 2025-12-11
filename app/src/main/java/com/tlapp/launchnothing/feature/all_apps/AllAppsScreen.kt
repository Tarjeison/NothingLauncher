package com.tlapp.launchnothing.feature.all_apps

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tlapp.launchnothing.data.models.AppInfo
import com.tlapp.launchnothing.ui.theme.AppTheme

@Composable
fun AllAppsScreen(modifier: Modifier = Modifier, viewModel: AllAppsViewModel = hiltViewModel()) {
    val apps by viewModel.apps.collectAsState()
    val expandedAppPackageName by viewModel.expandedAppPackageName.collectAsState()
    AppList(
        apps = apps,
        expandedAppPackageName = expandedAppPackageName,
        onAppLongPressed = viewModel::onAppLongPressed,
        onUninstallAppClicked = viewModel::onUninstallAppClicked,
        onDismissMenu = viewModel::onDismissMenu,
        modifier = modifier
    )
}

@Composable
private fun AppList(
    apps: List<AppInfo>,
    expandedAppPackageName: String?,
    onAppLongPressed: (String) -> Unit,
    onUninstallAppClicked: (String) -> Unit,
    onDismissMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(apps) { app ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                if (expandedAppPackageName == app.packageName) {
                                    onDismissMenu()
                                } else {
                                    val intent =
                                        context.packageManager.getLaunchIntentForPackage(app.packageName)
                                    context.startActivity(intent)
                                }
                            },
                            onLongClick = {
                                onAppLongPressed(app.packageName)
                            }
                        )
                        .padding(AppTheme.dimensions.paddingMedium)
                )
                if (expandedAppPackageName == app.packageName) {
                    Text(
                        text = "Uninstall",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clickable {
                                onUninstallAppClicked(app.packageName)
                            }
                            .padding(AppTheme.dimensions.paddingMedium)
                            .padding(start = AppTheme.dimensions.paddingMedium)
                    )
                }
            }
        }
    }
}
