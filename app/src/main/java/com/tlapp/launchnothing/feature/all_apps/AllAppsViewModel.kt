package com.tlapp.launchnothing.feature.all_apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlapp.launchnothing.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAppsViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    val apps = appRepository.apps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            appRepository.syncApps()
        }
    }
}
