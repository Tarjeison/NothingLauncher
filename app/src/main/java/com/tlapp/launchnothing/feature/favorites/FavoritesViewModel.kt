package com.tlapp.launchnothing.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlapp.launchnothing.data.models.AppInfo
import com.tlapp.launchnothing.data.repository.AppRepository
import com.tlapp.launchnothing.domain.usecase.ToggleFavoriteUseCase
import com.tlapp.launchnothing.domain.usecase.UninstallAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val apps: List<AppInfo> = emptyList(),
    val expandedAppPackageName: String? = null,
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val uninstallAppUseCase: UninstallAppUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _expandedAppPackageName = MutableStateFlow<String?>(null)

    val uiState = combine(
        appRepository.favoriteApps,
        _expandedAppPackageName,
    ) { favoriteApps, expandedAppPackageName ->
        FavoritesUiState(
            apps = favoriteApps,
            expandedAppPackageName = expandedAppPackageName,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesUiState(),
    )

    fun onAppLongPressed(
        packageName: String,
    ) {
        _expandedAppPackageName.value = packageName
    }

    fun onDismissMenu() {
        _expandedAppPackageName.value = null
    }

    fun onToggleFavorite(
        packageName: String,
        isFavorite: Boolean,
    ) {
        viewModelScope.launch {
            toggleFavoriteUseCase(packageName, isFavorite)
        }
    }

    fun onUninstallAppClicked(
        packageName: String,
    ) {
        uninstallAppUseCase(packageName)
        onDismissMenu()
    }
}
