package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CustomPinEntity::class], version = 1, exportSchema = false)
abstract class TarkovDatabase : RoomDatabase() {
    abstract fun customPinDao(): CustomPinDao

    companion object {
        @Volatile
        private var INSTANCE: TarkovDatabase? = null

        fun getDatabase(context: Context): TarkovDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TarkovDatabase::class.java,
                    "tarkov_radar.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
