package com.tlapp.launchnothing.feature.all_apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlapp.launchnothing.data.repository.AppRepository
import com.tlapp.launchnothing.di.IoDispatcher
import com.tlapp.launchnothing.domain.usecase.ToggleFavoriteUseCase
import com.tlapp.launchnothing.domain.usecase.UninstallAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAppsViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val uninstallAppUseCase: UninstallAppUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _expandedAppPackageName = MutableStateFlow<String?>(null)

    val uiState = combine(
        appRepository.apps,
        _expandedAppPackageName,
    ) { apps, expandedAppPackageName ->
        AllAppsUiState(
            apps = apps,
            expandedAppPackageName = expandedAppPackageName,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AllAppsUiState(),
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
        viewModelScope.launch(ioDispatcher) {
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
