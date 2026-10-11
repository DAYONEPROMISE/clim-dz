package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.climdz.app.AppViewModel
import com.climdz.app.Exposure
import com.climdz.app.Insulation
import com.climdz.app.R
import com.climdz.app.SizingEngine
import com.climdz.app.data.Intervention

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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(roomLabel, { roomLabel = it }, label = { Text(stringResource(R.string.room_label)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(clientName, { clientName = it }, label = { Text(stringResource(R.string.client_name)) }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(surface, { surface = it }, label = { Text(stringResource(R.string.surface)) }, modifier = Modifier.weight(1f))
            OutlinedTextField(height, { height = it }, label = { Text(stringResource(R.string.height)) }, modifier = Modifier.weight(1f))
            OutlinedTextField(occupants, { occupants = it }, label = { Text(stringResource(R.string.occupants)) }, modifier = Modifier.weight(1f))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.exposure))
            Exposure.values().forEach { e ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(e == exposure, { exposure = e })
                    Text(
                        when (e) {
                            Exposure.SHADED -> stringResource(R.string.exp_shaded)
                            Exposure.AVERAGE -> stringResource(R.string.exp_average)
                            Exposure.SUNNY -> stringResource(R.string.exp_sunny)
                        }
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.insulation))
            Insulation.values().forEach { s ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(s == insulation, { insulation = s })
                    Text(
                        when (s) {
                            Insulation.POOR -> stringResource(R.string.ins_poor)
                            Insulation.AVERAGE -> stringResource(R.string.ins_average)
                            Insulation.GOOD -> stringResource(R.string.ins_good)
                        }
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(kitchen, { kitchen = it }); Text(stringResource(R.string.kitchen))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(topFloor, { topFloor = it }); Text(stringResource(R.string.topfloor))
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
            Text("${stringResource(R.string.btu_needed)}: $btu BTU", style = MaterialTheme.typography.titleMedium)
            Text("${stringResource(R.string.power_kw)}: ${"%.2f".format(SizingEngine.kilowatts(btu))}")
            Text("${stringResource(R.string.recommended)}: $rec BTU", style = MaterialTheme.typography.titleMedium)
            Text(
                "${stringResource(R.string.checklist_title)} (${checked.size}/${checklist.size})",
                style = MaterialTheme.typography.titleMedium
            )
            checklist.forEachIndexed { i, label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(i in checked, {
                        checked = if (i in checked) checked - i else checked + i
                    })
                    Text(label)
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
