package com.example.rxmetrics.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "medicamentos",
    foreignKeys = [ForeignKey(
        entity = Doenca::class,
        parentColumns = ["id"],
        childColumns = ["doencaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("doencaId")]
)
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val doencaId: Long,
    val nome: String,
    val dose: String,
    val posologia: String,
    val via: String,
    val observacoes: String
)