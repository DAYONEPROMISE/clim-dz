package com.climdz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.climdz.app.ui.ClientsScreen
import com.climdz.app.ui.InterventionsScreen
import com.climdz.app.ui.SettingsScreen
import com.climdz.app.ui.SizingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ClimDzRoot() }
    }
}

fun applyLocale(tag: String, ctx: android.content.Context) {
    ctx.getSharedPreferences("climdz", MODE_PRIVATE).edit().putString("locale", tag).apply()
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
}

private const val MODE_PRIVATE = android.content.Context.MODE_PRIVATE

@Composable
fun ClimDzRoot() {
    val ctx = LocalContext.current
    var lang by remember {
        mutableStateOf(
            ctx.getSharedPreferences("climdz", MODE_PRIVATE).getString("locale", "fr") ?: "fr"
        )
    }
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        val nav = rememberNavController()
        val vm: AppViewModel = viewModel()
        val tabs = listOf("sizing", "clients", "fiches", "settings")
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    val entry by nav.currentBackStackEntryAsState()
                    val cur = entry?.destination?.route
                    tabs.forEach { t ->
                        val label = when (t) {
                            "sizing" -> stringResource(R.string.tab_sizing)
                            "clients" -> stringResource(R.string.tab_clients)
                            "fiches" -> stringResource(R.string.tab_interventions)
                            else -> stringResource(R.string.tab_settings)
                        }
                        val icon: ImageVector = when (t) {
                            "sizing" -> Icons.Filled.Calculate
                            "clients" -> Icons.Filled.Person
                            "fiches" -> Icons.Filled.List
                            else -> Icons.Filled.Settings
                        }
                        NavigationBarItem(
                            selected = cur == t,
                            onClick = { nav.navigate(t) { launchSingleTop = true } },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        ) { pad ->
            NavHost(nav, startDestination = "sizing", modifier = Modifier.padding(pad)) {
                composable("sizing") { SizingScreen(vm) }
                composable("clients") { ClientsScreen(vm) }
                composable("fiches") { InterventionsScreen(vm) }
                composable("settings") {
                    SettingsScreen(lang) {
                        lang = it
                        applyLocale(it, ctx)
                    }
                }
            }
        }
    }
}
