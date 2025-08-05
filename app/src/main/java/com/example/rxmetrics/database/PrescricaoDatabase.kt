package com.example.rxmetrics.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [Doenca::class, Medicamento::class],
    version = 1,
    exportSchema = false
)
abstract class PrescricaoDatabase : RoomDatabase() {

    abstract fun prescricaoDao(): PrescricaoDao

    companion object {
        @Volatile
        private var INSTANCE: PrescricaoDatabase? = null

        fun getDatabase(context: Context): PrescricaoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrescricaoDatabase::class.java,
                    "prescricao_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}