package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.climdz.app.AppViewModel
import com.climdz.app.Exposure
import com.climdz.app.Insulation
import com.climdz.app.R
import com.climdz.app.SizingEngine
import com.climdz.app.data.Intervention
import java.util.Locale

/** French grouping so BTU readouts stay readable (12 400 BTU). */
private fun fmt(n: Int): String = String.format(Locale.FRANCE, "%,d", n)

@Composable
fun SizingScreen(vm: AppViewModel) {
    var surface by remember { mutableStateOf("20") }
    var height by remember { mutableStateOf("2.8") }
    var exposure by remember { mutableStateOf(Exposure.AVERAGE) }
    var insulation by remember { mutableStateOf(Insulation.AVERAGE) }
    var occupants by remember { mutableStateOf("2") }
    var kitchen by remember { mutableStateOf(false) }
    var topFloor by remember { mutableStateOf(false) }
    var roomLabel by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Int?>(null) }
    val checklist = stringArrayResource(R.array.checklist_items)
    var checked by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionLabel(stringResource(R.string.section_room))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(roomLabel, { roomLabel = it }, label = { Text(stringResource(R.string.room_label)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(clientName, { clientName = it }, label = { Text(stringResource(R.string.client_name)) }, modifier = Modifier.fillMaxWidth())
        }

        SectionLabel(stringResource(R.string.section_load))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(surface, { surface = it }, label = { Text(stringResource(R.string.surface)) }, modifier = Modifier.weight(1f))
            OutlinedTextField(height, { height = it }, label = { Text(stringResource(R.string.height)) }, modifier = Modifier.weight(1f))
            OutlinedTextField(occupants, { occupants = it }, label = { Text(stringResource(R.string.occupants)) }, modifier = Modifier.weight(1f))
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.exposure), style = MaterialTheme.typography.titleSmall)
                Column(Modifier.selectableGroup()) {
                    Exposure.values().forEach { e ->
                        val label = when (e) {
                            Exposure.SHADED -> stringResource(R.string.exp_shaded)
                            Exposure.AVERAGE -> stringResource(R.string.exp_average)
                            Exposure.SUNNY -> stringResource(R.string.exp_sunny)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().selectable(selected = exposure == e, role = Role.RadioButton) { exposure = e }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = exposure == e, onClick = null)
                            Text(label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
                Text(stringResource(R.string.insulation), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
                Column(Modifier.selectableGroup()) {
                    Insulation.values().forEach { s ->
                        val label = when (s) {
                            Insulation.POOR -> stringResource(R.string.ins_poor)
                            Insulation.AVERAGE -> stringResource(R.string.ins_average)
                            Insulation.GOOD -> stringResource(R.string.ins_good)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().selectable(selected = insulation == s, role = Role.RadioButton) { insulation = s }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = insulation == s, onClick = null)
                            Text(label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }

        SectionLabel(stringResource(R.string.options_title))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)) {
                Row(
                            modifier = Modifier.fillMaxWidth().selectable(selected = kitchen, role = Role.Checkbox) { kitchen = !kitchen }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = kitchen, onCheckedChange = null)
                    Text(stringResource(R.string.kitchen), modifier = Modifier.padding(start = 8.dp))
                }
                Row(
                            modifier = Modifier.fillMaxWidth().selectable(selected = topFloor, role = Role.Checkbox) { topFloor = !topFloor }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = topFloor, onCheckedChange = null)
                    Text(stringResource(R.string.topfloor), modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Button(onClick = {
            result = SizingEngine.computeBtu(
                surface.toDoubleOrNull() ?: 20.0,
                height.toDoubleOrNull() ?: 2.8,
                exposure, insulation,
                occupants.toIntOrNull() ?: 2,
                kitchen, topFloor
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.calculate))
        }

        result?.let { btu ->
            val rec = SizingEngine.recommendedSize(btu)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        stringResource(R.string.readout_btu).uppercase(),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text("${fmt(btu)} BTU", style = MaterialTheme.typography.displaySmall)
                    Text(
                        "${stringResource(R.string.power_kw)}: ${String.format(Locale.FRANCE, "%.2f", SizingEngine.kilowatts(btu))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(stringResource(R.string.recommended).uppercase(), style = MaterialTheme.typography.labelMedium)
                    Text("${fmt(rec)} BTU", style = MaterialTheme.typography.headlineSmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.checklist_title), style = MaterialTheme.typography.titleSmall)
                    Text(
                        stringResource(R.string.checklist_progress, checked.size, checklist.size),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LinearProgressIndicator(
                        progress = if (checklist.isEmpty()) 0f else checked.size.toFloat() / checklist.size.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    checklist.forEachIndexed { i, label ->
                        Row(
                            modifier = Modifier.fillMaxWidth().selectable(selected = i in checked, role = Role.Checkbox) {
                                checked = if (i in checked) checked - i else checked + i
                            }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = i in checked, onCheckedChange = null)
                            Text(label, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Button(onClick = {
                vm.addIntervention(
                    Intervention(
                        clientName = clientName, dateEpoch = System.currentTimeMillis(),
                        roomLabel = roomLabel.ifBlank { "-" },
                        surface = surface.toDoubleOrNull() ?: 20.0,
                        height = height.toDoubleOrNull() ?: 2.8,
                        exposure = exposure.name, insulation = insulation.name,
                        occupants = occupants.toIntOrNull() ?: 2,
                        kitchen = kitchen, topFloor = topFloor,
                        btu = btu, recommended = rec,
                        kw = SizingEngine.kilowatts(btu),
                        checked = checked.sorted().joinToString(",")
                    )
                )
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_fiche))
            }
        }
    }
}
