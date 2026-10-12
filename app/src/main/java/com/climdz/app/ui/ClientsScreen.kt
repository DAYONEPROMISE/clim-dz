package com.climdz.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.climdz.app.AppViewModel
import com.climdz.app.R

@Composable
fun ClientsScreen(vm: AppViewModel, onNew: () -> Unit) {
    val clients by vm.clients.collectAsState()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text(stringResource(R.string.client_name)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(phone, { phone = it }, label = { Text(stringResource(R.string.client_phone)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(address, { address = it }, label = { Text(stringResource(R.string.client_address)) }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            if (name.isNotBlank()) {
                vm.addClient(name.trim(), phone.trim(), address.trim(), "")
                name = ""; phone = ""; address = ""
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.add_client))
        }
        if (clients.isEmpty()) {
            ClimEmptyState(
                title = stringResource(R.string.empty_clients_title),
                hint = stringResource(R.string.empty_clients_hint),
                action = {
                    Button(onClick = onNew, modifier = Modifier.padding(top = 8.dp)) {
                        Text(stringResource(R.string.new_sizing))
                    }
                }
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(clients, key = { it.id }) { c ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(c.name)
                                if (c.phone.isNotBlank()) Text(c.phone)
                                if (c.address.isNotBlank()) Text(c.address)
                            }
                            TextButton(onClick = { vm.deleteClient(c) }) {
                                Text(stringResource(R.string.delete))
                            }
                        }
                    }
                }
            }
        }
    }
}
