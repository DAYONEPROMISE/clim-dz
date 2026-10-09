package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.climdz.app.R

@Composable
fun SettingsScreen(lang: String, onLang: (String) -> Unit) {
    val checklist = stringArrayResource(R.array.checklist_items)
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.language))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(lang == "fr", { onLang("fr") })
            Text(stringResource(R.string.lang_fr))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(lang == "ar", { onLang("ar") })
            Text(stringResource(R.string.lang_ar))
        }
        Text(stringResource(R.string.checklist_title))
        checklist.forEach { item -> Text("- " + item) }
        Text(stringResource(R.string.about))
    }
}
