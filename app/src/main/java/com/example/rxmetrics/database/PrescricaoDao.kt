// PrescricaoDao.kt
package com.example.rxmetrics.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescricaoDao {

    // Inserir doença
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirDoenca(doenca: Doenca): Long

    // Inserir medicamento
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirMedicamento(medicamento: Medicamento): Long

    // Buscar doença por nome
    @Query("SELECT * FROM doencas WHERE LOWER(nome) = LOWER(:nome) LIMIT 1")
    suspend fun buscarDoencaPorNome(nome: String): Doenca?

    // Buscar todas as doenças com medicamentos
    @Transaction
    @Query("SELECT * FROM doencas ORDER BY nome ASC")
    fun obterTodasDoencasComMedicamentos(): Flow<List<DoencaComMedicamentos>>

    // Buscar doenças e medicamentos por termo de busca
    @Transaction
    @Query("""
        SELECT DISTINCT d.* FROM doencas d 
        LEFT JOIN medicamentos m ON d.id = m.doencaId 
        WHERE LOWER(d.nome) LIKE '%' || LOWER(:termo) || '%' 
        OR LOWER(m.nome) LIKE '%' || LOWER(:termo) || '%'
        ORDER BY d.nome ASC
    """)
    fun buscarPorTermo(termo: String): Flow<List<DoencaComMedicamentos>>

    // Deletar medicamento
    @Delete
    suspend fun deletarMedicamento(medicamento: Medicamento)

    // Deletar doença (cascata deletará medicamentos)
    @Delete
    suspend fun deletarDoenca(doenca: Doenca)

    // Verificar se doença existe
    @Query("SELECT COUNT(*) FROM doencas WHERE LOWER(nome) = LOWER(:nome)")
    suspend fun doencaExiste(nome: String): Int

    // Contar medicamentos de uma doença
    @Query("SELECT COUNT(*) FROM medicamentos WHERE doencaId = :doencaId")
    suspend fun contarMedicamentosPorDoenca(doencaId: Long): Int
}