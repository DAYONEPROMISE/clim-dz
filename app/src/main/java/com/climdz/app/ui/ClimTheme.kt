package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * ClimDZ "Dimensionnement" v3 — warm gray canvas, refined teal primary,
 * soft coral for main actions.
 * Tokens from Shared/OpenPencil/clim-dz/design-v3.html (design source of truth).
 */
private val WarmLight = lightColorScheme(
    primary = Color(0xFF0F6E64),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE4F2EF),
    onPrimaryContainer = Color(0xFF005049),
    secondary = Color(0xFF4A635E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8E1),
    onSecondaryContainer = Color(0xFF07201C),
    tertiary = Color(0xFFC24A36),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFBEAE5),
    onTertiaryContainer = Color(0xFF5C1900),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFF5F3F0),
    onBackground = Color(0xFF1C1B1A),
    surface = Color(0xFFF5F3F0),
    onSurface = Color(0xFF1C1B1A),
    surfaceVariant = Color(0xFFE8E4DE),
    onSurfaceVariant = Color(0xFF4A4642),
    outline = Color(0xFF7C7772),
    outlineVariant = Color(0xFFD6D1CA),
    inverseSurface = Color(0xFF302E2B),
    inverseOnSurface = Color(0xFFF3EFEA)
)

private val WarmDark = darkColorScheme(
    primary = Color(0xFF61D6C8),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF005049),
    onPrimaryContainer = Color(0xFFB9F0E7),
    secondary = Color(0xFFB1CCC6),
    onSecondary = Color(0xFF1C352F),
    secondaryContainer = Color(0xFF334B45),
    onSecondaryContainer = Color(0xFFCCE8E1),
    tertiary = Color(0xFFFFB59D),
    onTertiary = Color(0xFF5C1900),
    tertiaryContainer = Color(0xFF7A2B15),
    onTertiaryContainer = Color(0xFFFFDBD1),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF141311),
    onBackground = Color(0xFFE8E3DD),
    surface = Color(0xFF141311),
    onSurface = Color(0xFFE8E3DD),
    surfaceVariant = Color(0xFF241F1C),
    onSurfaceVariant = Color(0xFFCFC8C1),
    outline = Color(0xFF98928C),
    outlineVariant = Color(0xFF4A4642),
    inverseSurface = Color(0xFFE8E3DD),
    inverseOnSurface = Color(0xFF302E2B)
)

@Composable
fun ClimDzTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) WarmDark else WarmLight,
        typography = MaterialTheme.typography,
        content = content
    )
}

/**
 * App bar on the warm canvas. Surface + status-bar padding keeps it identical
 * across Material3 versions.
 */
@Composable
fun ClimHeader(title: String, subtitle: String, modifier: Modifier = Modifier, trailing: (@Composable () -> Unit)? = null) {
    Surface(color = MaterialTheme.colorScheme.surface, modifier = modifier) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().height(72.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            trailing?.invoke()
        }
    }
}

/** Numbered section: "01" marker in teal plus a title, wrapping a card of fields. */
@Composable
fun NumberedSection(number: String, title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        ) {
            Text(
                number,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(title, style = MaterialTheme.typography.titleSmall)
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp)) { content() }
        }
    }
}

/** Elevated white/dark card used for readouts and lists. */
@Composable
fun ClimCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) { content() }
    }
}

/** Empty state with a tinted glyph badge; the badge is decorative. */
@Composable
fun ClimEmptyState(
    icon: ImageVector,
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
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(50),
            modifier = Modifier.size(96.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(44.dp)
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

/** Round initials badge used in the client list. */
@Composable
fun InitialsBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(50),
        modifier = modifier.size(42.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/** Primary action button — coral, per the v3 design system. */
@Composable
fun CoralButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}
