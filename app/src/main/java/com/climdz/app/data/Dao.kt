package com.climdz.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun all(): Flow<List<Client>>

    @Insert
    suspend fun insert(c: Client): Long

    @Delete
    suspend fun delete(c: Client)
}

@Dao
interface InterventionDao {
    @Query("SELECT * FROM interventions ORDER BY dateEpoch DESC")
    fun all(): Flow<List<Intervention>>

    @Insert
    suspend fun insert(i: Intervention): Long

    @Delete
    suspend fun delete(i: Intervention)
}
