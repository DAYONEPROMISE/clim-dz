package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NumberedSection(stringResource(R.string.section_number_1), stringResource(R.string.section_room)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = roomLabel,
                    onValueChange = { roomLabel = it },
                    label = { Text(stringResource(R.string.room_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text(stringResource(R.string.client_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        NumberedSection(stringResource(R.string.section_number_2), stringResource(R.string.section_load)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = surface,
                        onValueChange = { surface = it },
                        label = { Text(stringResource(R.string.surface)) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text(stringResource(R.string.height)) },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = occupants,
                    onValueChange = { occupants = it },
                    label = { Text(stringResource(R.string.occupants)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    stringResource(R.string.exposure),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Exposure.entries.forEach { e ->
                        val label = when (e) {
                            Exposure.SHADED -> stringResource(R.string.exp_shaded)
                            Exposure.AVERAGE -> stringResource(R.string.exp_average)
                            Exposure.SUNNY -> stringResource(R.string.exp_sunny)
                        }
                        ChoiceChip(
                            selected = exposure == e,
                            onClick = { exposure = e },
                            label = label
                        )
                    }
                }

                Text(
                    stringResource(R.string.insulation),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Insulation.entries.forEach { s ->
                        val label = when (s) {
                            Insulation.POOR -> stringResource(R.string.ins_poor)
                            Insulation.AVERAGE -> stringResource(R.string.ins_average)
                            Insulation.GOOD -> stringResource(R.string.ins_good)
                        }
                        ChoiceChip(
                            selected = insulation == s,
                            onClick = { insulation = s },
                            label = label
                        )
                    }
                }
            }
        }

        NumberedSection(stringResource(R.string.section_number_3), stringResource(R.string.options_title)) {
            Column {
                ChoiceCheckRow(
                    selected = kitchen,
                    onToggle = { kitchen = !kitchen },
                    label = stringResource(R.string.kitchen)
                )
                ChoiceCheckRow(
                    selected = topFloor,
                    onToggle = { topFloor = !topFloor },
                    label = stringResource(R.string.topfloor)
                )
            }
        }

        CoralButton(
            onClick = {
                result = SizingEngine.computeBtu(
                    surface.toDoubleOrNull() ?: 20.0,
                    height.toDoubleOrNull() ?: 2.8,
                    exposure, insulation,
                    occupants.toIntOrNull() ?: 2,
                    kitchen, topFloor
                )
            },
            text = stringResource(R.string.calculate_cta)
        )

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
                    Text(
                        stringResource(R.string.recommended).uppercase(),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text("${fmt(rec)} BTU", style = MaterialTheme.typography.headlineSmall)
                }
            }

            ClimCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        ChoiceCheckRow(
                            selected = i in checked,
                            onToggle = { checked = if (i in checked) checked - i else checked + i },
                            label = label,
                            compact = true
                        )
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

/** Selectable chip (replaces radio buttons in the v3 system). */
@Composable
private fun ChoiceChip(selected: Boolean, onClick: () -> Unit, label: String) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

/** Checkbox row driven by the whole-row click target. */
@Composable
private fun ChoiceCheckRow(
    selected: Boolean,
    onToggle: () -> Unit,
    label: String,
    compact: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.Checkbox) { onToggle() }
            .padding(vertical = if (compact) 4.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = selected, onCheckedChange = null)
        Text(
            label,
            style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
