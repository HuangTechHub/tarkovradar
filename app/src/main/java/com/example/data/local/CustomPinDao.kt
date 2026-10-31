package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomPinDao {
    @Query("SELECT * FROM custom_pins WHERE mapId = :mapId ORDER BY timestamp DESC")
    fun getPinsForMap(mapId: String): Flow<List<CustomPinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPin(pin: CustomPinEntity): Long

    @Delete
    suspend fun deletePin(pin: CustomPinEntity)

    @Query("DELETE FROM custom_pins WHERE id = :pinId")
    suspend fun deletePinById(pinId: Long)
}
