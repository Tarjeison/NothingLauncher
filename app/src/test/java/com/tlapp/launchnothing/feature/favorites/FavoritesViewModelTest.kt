package com.tlapp.launchnothing.feature.favorites

import app.cash.turbine.test
import com.tlapp.launchnothing.data.models.AppInfo
import com.tlapp.launchnothing.data.repository.AppRepository
import com.tlapp.launchnothing.domain.usecase.ToggleFavoriteUseCase
import com.tlapp.launchnothing.domain.usecase.UninstallAppUseCase
import com.tlapp.launchnothing.feature.all_apps.AllAppsUiState
import com.tlapp.launchnothing.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val appRepository: AppRepository = mockk()
    private val uninstallAppUseCase: UninstallAppUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()

    private lateinit var viewModel: FavoritesViewModel

    private val favoriteAppsFlow = MutableStateFlow<List<AppInfo>>(emptyList())

    @Before
    fun setup() {
        every { appRepository.favoriteApps } returns favoriteAppsFlow
        viewModel = FavoritesViewModel(
            appRepository = appRepository,
            uninstallAppUseCase = uninstallAppUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun `uiState initially emits default state`() = runTest {
        viewModel.uiState.test {
            assertEquals(FavoritesUiState(), awaitItem())
        }
    }

    @Test
    fun `uiState updates when favoriteApps flow emits`() = runTest {
        val testApps = listOf(
            AppInfo(packageName = "p1", label = "L1", isSystemApp = false, isFavorite = true)
        )
        
        viewModel.uiState.test {
            assertEquals(FavoritesUiState(), awaitItem())
            favoriteAppsFlow.value = testApps
            assertEquals(FavoritesUiState(apps = testApps), awaitItem())
        }
    }

    @Test
    fun `onAppLongPressed updates expandedAppPackageName in uiState`() = runTest {
        viewModel.uiState.test {
            assertEquals(FavoritesUiState(), awaitItem())
            viewModel.onAppLongPressed("p1")
            assertEquals(FavoritesUiState(expandedAppPackageName = "p1"), awaitItem())
        }
    }

    @Test
    fun `onDismissMenu resets expandedAppPackageName in uiState`() = runTest {
        viewModel.uiState.test {
            assertEquals(FavoritesUiState(), awaitItem())
            viewModel.onAppLongPressed("p1")
            assertEquals(FavoritesUiState(expandedAppPackageName = "p1"), awaitItem())
            viewModel.onDismissMenu()
            assertEquals(FavoritesUiState(expandedAppPackageName = null), awaitItem())
        }
    }

    @Test
    fun `onToggleFavorite calls toggleFavoriteUseCase`() = runTest {
        coEvery { toggleFavoriteUseCase("p1", false) } just runs
        viewModel.onToggleFavorite("p1", false)
        coVerify { toggleFavoriteUseCase("p1", false) }
    }

    @Test
    fun `onUninstallAppClicked calls uninstallAppUseCase and dismisses menu`() = runTest {
        every { uninstallAppUseCase("p1") } just runs
        viewModel.uiState.test {
            assertEquals(FavoritesUiState(), awaitItem())
            viewModel.onAppLongPressed("p1")
            assertEquals(FavoritesUiState(expandedAppPackageName = "p1"), awaitItem())
            
            viewModel.onUninstallAppClicked("p1")
            
            verify { uninstallAppUseCase("p1") }
            assertEquals(FavoritesUiState(expandedAppPackageName = null), awaitItem())
        }
    }
}
