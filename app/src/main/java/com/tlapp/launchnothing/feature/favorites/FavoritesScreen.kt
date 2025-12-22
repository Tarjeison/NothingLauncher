package com.tlapp.launchnothing.feature.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tlapp.launchnothing.common.ui.AppList

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AppList(
        apps = uiState.apps,
        expandedAppPackageName = uiState.expandedAppPackageName,
        onAppLongPressed = viewModel::onAppLongPressed,
        onUninstallAppClicked = viewModel::onUninstallAppClicked,
        onToggleFavorite = viewModel::onToggleFavorite,
        onDismissMenu = viewModel::onDismissMenu,
        modifier = modifier,
    )
}
