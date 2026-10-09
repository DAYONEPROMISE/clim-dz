package com.climdz.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val notes: String = ""
)

@Entity(tableName = "interventions")
data class Intervention(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long = 0,
    val clientName: String = "",
    val dateEpoch: Long = 0,
    val roomLabel: String = "",
    val surface: Double = 0.0,
    val height: Double = 2.6,
    val exposure: String = "AVERAGE",
    val insulation: String = "AVERAGE",
    val occupants: Int = 2,
    val kitchen: Boolean = false,
    val topFloor: Boolean = false,
    val btu: Int = 0,
    val recommended: Int = 0,
    val kw: Double = 0.0,
    /** checked checklist indices, e.g. "0,3,5" */
    val checked: String = "",
    val notes: String = ""
)
