package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.climdz.app.AppViewModel
import com.climdz.app.R
import com.climdz.app.shareInterventionPdf

@Composable
fun InterventionsScreen(vm: AppViewModel, onNew: () -> Unit) {
    val items by vm.interventions.collectAsState()
    val ctx = LocalContext.current
    val checklist = stringArrayResource(R.array.checklist_items).toList()
    Column(modifier = Modifier.padding(16.dp)) {
        if (items.isEmpty()) {
            ClimEmptyState(
                icon = Icons.Default.Assignment,
                title = stringResource(R.string.empty_fiches_title),
                hint = stringResource(R.string.empty_fiches_hint),
                action = {
                    Button(onClick = onNew, modifier = Modifier.padding(top = 8.dp)) {
                        Text(stringResource(R.string.new_sizing))
                    }
                }
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { f ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${f.clientName} - ${f.roomLabel}")
                            Text("${f.surface} m2 | ${f.btu} BTU -> ${f.recommended} BTU")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { shareInterventionPdf(ctx, f, checklist) }) {
                                    Text(stringResource(R.string.share_pdf))
                                }
                                TextButton(onClick = { vm.deleteIntervention(f) }) {
                                    Text(stringResource(R.string.delete))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
