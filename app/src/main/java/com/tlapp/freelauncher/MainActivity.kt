package com.tlapp.freelauncher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tlapp.freelauncher.ui.theme.LaunchNothingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchNothingTheme {
                enableEdgeToEdge()
                Box(modifier = Modifier.systemBarsPadding()) {
                    AppLauncher()
                }
            }
        }
    }
}

@Composable
fun AppLauncher(viewModel: AllAppsViewModel = viewModel()) {
    val apps by viewModel.apps.collectAsState()
    AppList(apps = apps)
}

@Composable
fun AppList(apps: List<AppInfo>) {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(apps) {
            app ->
            Row(
                modifier = Modifier
                    .clickable {
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(app.packageName.toString())
                        context.startActivity(intent)
                    }
                    .padding(8.dp)
            ) {
                Image(
                    bitmap = app.icon.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = app.label.toString())
                }
            }
        }
    }
}
