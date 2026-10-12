package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * ClimDZ "Frigo" identity — cold instrument panel.
 * Tokens from Shared/OpenPencil/clim-dz/design-v2.html (design source of truth).
 */
private val FrigoLight = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF005E9E),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD3E7FA),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF001D33),
    secondary = androidx.compose.ui.graphics.Color(0xFF006877),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFB8EBF5),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF001F25),
    tertiary = androidx.compose.ui.graphics.Color(0xFFA03A10),
    onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBCB),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF340100),
    error = androidx.compose.ui.graphics.Color(0xFFB3261E),
    onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    errorContainer = androidx.compose.ui.graphics.Color(0xFFF9DEDC),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410E0B),
    background = androidx.compose.ui.graphics.Color(0xFFF5F9FC),
    onBackground = androidx.compose.ui.graphics.Color(0xFF171C1F),
    surface = androidx.compose.ui.graphics.Color(0xFFF5F9FC),
    onSurface = androidx.compose.ui.graphics.Color(0xFF171C1F),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFDDE5EC),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF41484D),
    outline = androidx.compose.ui.graphics.Color(0xFFBFC8CF),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFCFD8DF),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFF2C3134),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFFEDF1F3)
)

private val FrigoDark = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF9CCBFA),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF003257),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF00497C),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD3E7FA),
    secondary = androidx.compose.ui.graphics.Color(0xFF83D2E0),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF00363D),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF004E5A),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFB8EBF5),
    tertiary = androidx.compose.ui.graphics.Color(0xFFFFB59B),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF5C1900),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF7A2A08),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBCB),
    error = androidx.compose.ui.graphics.Color(0xFFF2B8B5),
    onError = androidx.compose.ui.graphics.Color(0xFF601410),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF8C1D18),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFF9DEDC),
    background = androidx.compose.ui.graphics.Color(0xFF0E1418),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE1E6EA),
    surface = androidx.compose.ui.graphics.Color(0xFF0E1418),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE1E6EA),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF41484D),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC0C8CD),
    outline = androidx.compose.ui.graphics.Color(0xFF8A9297),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF41484D),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFFE1E6EA),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFF2C3134)
)

@Composable
fun ClimDzTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) FrigoDark else FrigoLight,
        typography = MaterialTheme.typography,
        content = content
    )
}

/** Small uppercase section heading used above each input group. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

/**
 * Offline-first empty state: snowflake glyph, title, hint.
 * The glyph carries no meaning beyond decoration, so it is hidden from a11y.
 */
@Composable
fun ClimEmptyState(
    title: String,
    hint: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(50),
            modifier = Modifier.size(96.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "❄",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = ""
                    }
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        action?.invoke()
    }
}
