package com.example.rxmetrics.database

import kotlinx.coroutines.flow.Flow

class PrescricaoRepository(private val dao: PrescricaoDao) {

    // Obter todas as prescrições
    fun obterTodasPrescricoes(): Flow<List<DoencaComMedicamentos>> {
        return dao.obterTodasDoencasComMedicamentos()
    }

    // Buscar prescrições por termo
    fun buscarPrescricoes(termo: String): Flow<List<DoencaComMedicamentos>> {
        return dao.buscarPorTermo(termo)
    }

    // Adicionar nova prescrição
    suspend fun adicionarPrescricao(
        nomeDoenca: String,
        nomeMedicamento: String,
        dose: String,
        posologia: String,
        via: String,
        observacoes: String
    ): Boolean {
        return try {
            // Verificar se doença já existe
            var doenca = dao.buscarDoencaPorNome(nomeDoenca.trim())

            // Se não existe, criar nova doença
            if (doenca == null) {
                val novaDoenca = Doenca(nome = nomeDoenca.trim())
                val doencaId = dao.inserirDoenca(novaDoenca)
                doenca = novaDoenca.copy(id = doencaId)
            }

            // Criar novo medicamento
            val novoMedicamento = Medicamento(
                doencaId = doenca.id,
                nome = nomeMedicamento.trim(),
                dose = dose.trim(),
                posologia = posologia.trim(),
                via = via.trim(),
                observacoes = observacoes.trim()
            )

            dao.inserirMedicamento(novoMedicamento)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Deletar medicamento
    suspend fun deletarMedicamento(medicamento: Medicamento): Boolean {
        return try {
            dao.deletarMedicamento(medicamento)

            // Se não há mais medicamentos para esta doença, deletar a doença também
            val count = dao.contarMedicamentosPorDoenca(medicamento.doencaId)
            if (count == 0) {
                val doenca = Doenca(id = medicamento.doencaId, nome = "")
                dao.deletarDoenca(doenca)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Verificar se doença existe
    suspend fun verificarSeDoencaExiste(nome: String): Boolean {
        return dao.doencaExiste(nome.trim()) > 0
    }
}