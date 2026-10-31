package com.example.data

import com.example.data.local.CustomPinDao
import com.example.data.local.CustomPinEntity
import com.example.model.AmmoEntry
import com.example.model.KeyEntry
import com.example.model.TarkovMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TarkovRepository(
    private val pinDao: CustomPinDao
) {
    fun getAllMaps(): List<TarkovMap> = TarkovPresetsData.allMaps

    fun getMapById(id: String): TarkovMap? {
        return TarkovPresetsData.allMaps.find { it.id == id } ?: TarkovPresetsData.allMaps.firstOrNull()
    }

    fun getCustomPinsForMap(mapId: String): Flow<List<CustomPinEntity>> {
        return pinDao.getPinsForMap(mapId)
    }

    suspend fun addCustomPin(pin: CustomPinEntity): Long {
        return pinDao.insertPin(pin)
    }

    suspend fun removeCustomPin(pin: CustomPinEntity) {
        pinDao.deletePin(pin)
    }

    suspend fun removePinById(pinId: Long) {
        pinDao.deletePinById(pinId)
    }

    fun getAllAmmo(): List<AmmoEntry> = TarkovPresetsData.ammoList

    fun getAllKeys(): List<KeyEntry> = TarkovPresetsData.keysList
}
