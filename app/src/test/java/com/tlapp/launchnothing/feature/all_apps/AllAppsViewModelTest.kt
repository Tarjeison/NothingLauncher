package com.tlapp.launchnothing.feature.all_apps

import app.cash.turbine.test
import com.tlapp.launchnothing.data.models.AppInfo
import com.tlapp.launchnothing.data.repository.AppRepository
import com.tlapp.launchnothing.domain.usecase.ToggleFavoriteUseCase
import com.tlapp.launchnothing.domain.usecase.UninstallAppUseCase
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
class AllAppsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val appRepository: AppRepository = mockk()
    private val uninstallAppUseCase: UninstallAppUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()

    private lateinit var viewModel: AllAppsViewModel

    private val appsFlow = MutableStateFlow<List<AppInfo>>(emptyList())

    @Before
    fun setup() {
        every { appRepository.apps } returns appsFlow
        viewModel = AllAppsViewModel(
            appRepository = appRepository,
            uninstallAppUseCase = uninstallAppUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            ioDispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun `uiState initially emits default state`() = runTest {
        viewModel.uiState.test {
            assertEquals(AllAppsUiState(), awaitItem())
        }
    }

    @Test
    fun `uiState updates when apps flow emits`() = runTest {
        val testApps = listOf(
            AppInfo(packageName = "p1", label = "L1", isSystemApp = false, isFavorite = false)
        )
        
        viewModel.uiState.test {
            assertEquals(AllAppsUiState(), awaitItem())
            appsFlow.value = testApps
            assertEquals(AllAppsUiState(apps = testApps), awaitItem())
        }
    }

    @Test
    fun `onAppLongPressed updates expandedAppPackageName in uiState`() = runTest {
        viewModel.uiState.test {
            assertEquals(AllAppsUiState(), awaitItem())
            viewModel.onAppLongPressed("p1")
            assertEquals(AllAppsUiState(expandedAppPackageName = "p1"), awaitItem())
        }
    }

    @Test
    fun `onDismissMenu resets expandedAppPackageName in uiState`() = runTest {
        viewModel.uiState.test {
            assertEquals(AllAppsUiState(), awaitItem())
            viewModel.onAppLongPressed("p1")
            assertEquals(AllAppsUiState(expandedAppPackageName = "p1"), awaitItem())
            viewModel.onDismissMenu()
            assertEquals(AllAppsUiState(expandedAppPackageName = null), awaitItem())
        }
    }

    @Test
    fun `onToggleFavorite calls toggleFavoriteUseCase`() = runTest {
        coEvery { toggleFavoriteUseCase("p1", true) } just runs
        viewModel.onToggleFavorite("p1", true)
        coVerify { toggleFavoriteUseCase("p1", true) }
    }

    @Test
    fun `onUninstallAppClicked calls uninstallAppUseCase and dismisses menu`() = runTest {
        every { uninstallAppUseCase("p1") } just runs
        viewModel.uiState.test {
            assertEquals(AllAppsUiState(), awaitItem())
            viewModel.onAppLongPressed("p1")
            assertEquals(AllAppsUiState(expandedAppPackageName = "p1"), awaitItem())
            
            viewModel.onUninstallAppClicked("p1")
            
            verify { uninstallAppUseCase("p1") }
            assertEquals(AllAppsUiState(expandedAppPackageName = null), awaitItem())
        }
    }
}
