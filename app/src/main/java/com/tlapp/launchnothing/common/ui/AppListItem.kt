package com.tlapp.launchnothing.common.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tlapp.launchnothing.data.models.AppInfo
import com.tlapp.launchnothing.ui.theme.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    app: AppInfo,
    isExpanded: Boolean,
    onAppClick: () -> Unit,
    onAppLongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onUninstallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.animateContentSize(),
    ) {
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onAppClick,
                    onLongClick = onAppLongClick,
                )
                .padding(AppTheme.dimensions.paddingMedium),
        )
        if (isExpanded) {
            Text(
                text = if (app.isFavorite) "Unfavorite" else "Favorite",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable { onToggleFavorite() }
                    .padding(AppTheme.dimensions.paddingMedium)
                    .padding(start = AppTheme.dimensions.paddingMedium),
            )
            if (!app.isSystemApp) {
                Text(
                    text = "Uninstall",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable { onUninstallClick() }
                        .padding(AppTheme.dimensions.paddingMedium)
                        .padding(start = AppTheme.dimensions.paddingMedium),
                )
            }
        }
    }
}
