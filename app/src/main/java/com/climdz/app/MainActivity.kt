package com.climdz.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.climdz.app.ui.ClientsScreen
import com.climdz.app.ui.ClimDzTheme
import com.climdz.app.ui.ClimHeader
import com.climdz.app.ui.InterventionsScreen
import com.climdz.app.ui.SettingsScreen
import com.climdz.app.ui.SizingScreen

private const val MODE_PRIVATE = android.content.Context.MODE_PRIVATE

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

/** Switch language: persist, ask the system for the locale, then rebuild the UI. */
private fun Context.switchLanguage(tag: String) {
    applyLocale(tag, this)
    (this as? android.app.Activity)?.recreate()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimDzRoot() {
    val ctx = LocalContext.current
    val activity = ctx as? android.app.Activity
    var lang by remember {
        mutableStateOf(
            ctx.getSharedPreferences("climdz", MODE_PRIVATE).getString("locale", "fr") ?: "fr"
        )
    }
    fun switchLang(next: String) {
        lang = next
        ctx.switchLanguage(next)
        activity?.recreate()
    }
    ClimDzTheme(dark = isSystemInDarkTheme()) {
        val nav = rememberNavController()
        val vm: AppViewModel = viewModel()
        val tabs = listOf("sizing", "clients", "fiches", "settings")
        val entry by nav.currentBackStackEntryAsState()
        val cur = entry?.destination?.route
        val title = when (cur) {
            "sizing" -> stringResource(R.string.tab_sizing)
            "clients" -> stringResource(R.string.tab_clients)
            "fiches" -> stringResource(R.string.tab_interventions)
            else -> stringResource(R.string.tab_settings)
        }
        val subtitle = when (cur) {
            "sizing" -> stringResource(R.string.app_subtitle)
            "clients" -> stringResource(R.string.clients_subtitle)
            "fiches" -> stringResource(R.string.fiches_subtitle)
            else -> stringResource(R.string.about)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ClimHeader(
                    title = title,
                    subtitle = subtitle,
                    trailing = {
                        TextButton(onClick = {
                            switchLang(if (lang == "fr") "ar" else "fr")
                        }) {
                            Text(
                                if (lang == "fr") stringResource(R.string.lang_ar)
                                else stringResource(R.string.lang_fr)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
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
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        ) { pad ->
            NavHost(nav, startDestination = "sizing", modifier = Modifier.padding(pad)) {
                composable("sizing") { SizingScreen(vm) }
                composable("clients") {
                    ClientsScreen(vm) { nav.navigate("sizing") { launchSingleTop = true } }
                }
                composable("fiches") {
                    InterventionsScreen(vm) { nav.navigate("sizing") { launchSingleTop = true } }
                }
                composable("settings") {
                    SettingsScreen(lang) { next -> switchLang(next) }
                }
            }
        }
    }
}
