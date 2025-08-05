package com.example.rxmetrics.database

import androidx.room.Embedded
import androidx.room.Relation

data class DoencaComMedicamentos(
    @Embedded val doenca: Doenca,
    @Relation(
        parentColumn = "id",
        entityColumn = "doencaId"
    )
    val medicamentos: List<Medicamento>
)