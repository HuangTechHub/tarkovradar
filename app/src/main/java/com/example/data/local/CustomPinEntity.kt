package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_pins")
data class CustomPinEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mapId: String,
    val title: String,
    val note: String,
    val x: Float,
    val y: Float,
    val floorIndex: Int,
    val tagType: String, // e.g. "AIRDROP", "DEAD_BODY", "STASH", "SNIPER", "DANGER"
    val timestamp: Long = System.currentTimeMillis()
)
