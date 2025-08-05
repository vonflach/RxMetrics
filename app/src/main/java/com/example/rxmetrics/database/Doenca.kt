package com.example.rxmetrics.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doencas")
data class Doenca(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String
)