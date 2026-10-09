package com.climdz.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.climdz.app.data.AppDatabase
import com.climdz.app.data.Client
import com.climdz.app.data.Intervention
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)

    val clients: StateFlow<List<Client>> =
        db.clientDao().all().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val interventions: StateFlow<List<Intervention>> =
        db.interventionDao().all().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addClient(name: String, phone: String, address: String, notes: String) {
        viewModelScope.launch {
            db.clientDao().insert(Client(name = name, phone = phone, address = address, notes = notes))
        }
    }

    fun deleteClient(c: Client) {
        viewModelScope.launch { db.clientDao().delete(c) }
    }

    fun addIntervention(i: Intervention) {
        viewModelScope.launch { db.interventionDao().insert(i) }
    }

    fun deleteIntervention(i: Intervention) {
        viewModelScope.launch { db.interventionDao().delete(i) }
    }
}
