package com.tlapp.launchnothing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tlapp.launchnothing.data.models.AppInfo
import com.tlapp.launchnothing.ui.theme.AppTheme
import com.tlapp.launchnothing.ui.theme.LaunchNothingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchNothingTheme {
                Scaffold { innerPadding ->
                    AppLauncher(
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppLauncher(modifier: Modifier = Modifier, viewModel: AllAppsViewModel = hiltViewModel()) {
    val apps by viewModel.apps.collectAsState()
    AppList(apps = apps, modifier = modifier)
}

@Composable
fun AppList(apps: List<AppInfo>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(apps) { app ->
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .clickable {
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(app.packageName)
                        context.startActivity(intent)
                    }
                    .padding(AppTheme.dimensions.paddingSmall)
            )
        }
    }
}
