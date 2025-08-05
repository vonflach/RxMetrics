package com.example.rxmetrics

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job
import com.example.rxmetrics.database.*
import com.example.rxmetrics.database.PrescricaoRepository
import com.example.rxmetrics.database.PrescricaoDatabase
import com.example.rxmetrics.database.Medicamento
import com.example.rxmetrics.database.Doenca
import com.example.rxmetrics.database.DoencaComMedicamentos

class PrescricoesActivity : AppCompatActivity() {

    // Views
    private lateinit var searchView: SearchView
    private lateinit var btnToggleForm: Button
    private lateinit var scrollViewForm: ScrollView
    private lateinit var etDoenca: TextInputEditText
    private lateinit var etMedicamento: TextInputEditText
    private lateinit var etDose: TextInputEditText
    private lateinit var etPosologia: TextInputEditText
    private lateinit var etVia: TextInputEditText
    private lateinit var etObservacoes: TextInputEditText
    private lateinit var btnAdicionar: Button
    private lateinit var btnCancelar: Button
    private lateinit var recyclerView: RecyclerView

    // Database
    private lateinit var repository: PrescricaoRepository
    private lateinit var adapter: PrescricoesAdapter

    // Para controlar a observação de dados
    private var observeJob: Job? = null
    private var currentSearchTerm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar handler global para exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            exception.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Erro crítico detectado. Reporte este bug!", Toast.LENGTH_LONG).show()
            }
        }

        setContentView(R.layout.activity_prescricoes)

        try {
            initViews()
            setupDatabase()
            setupRecyclerView()
            setupListeners()
            observeData()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao inicializar: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        searchView = findViewById(R.id.searchView)
        btnToggleForm = findViewById(R.id.btnToggleForm)
        scrollViewForm = findViewById(R.id.scrollViewForm)
        etDoenca = findViewById(R.id.etDoenca)
        etMedicamento = findViewById(R.id.etMedicamento)
        etDose = findViewById(R.id.etDose)
        etPosologia = findViewById(R.id.etPosologia)
        etVia = findViewById(R.id.etVia)
        etObservacoes = findViewById(R.id.etObservacoes)
        btnAdicionar = findViewById(R.id.btnAdicionar)
        btnCancelar = findViewById(R.id.btnCancelar)
        recyclerView = findViewById(R.id.recyclerViewPrescricoes)
    }

    private fun setupDatabase() {
        val database = PrescricaoDatabase.getDatabase(this)
        repository = PrescricaoRepository(database.prescricaoDao())
    }

    private fun setupRecyclerView() {
        adapter = PrescricoesAdapter { medicamento ->
            // Callback para deletar medicamento
            showDeleteConfirmationDialog(medicamento)
        }

        recyclerView.apply {
            this.adapter = this@PrescricoesActivity.adapter
            layoutManager = LinearLayoutManager(this@PrescricoesActivity)
            // Configurações para melhor estabilidade
            setHasFixedSize(false)
            setItemViewCacheSize(0) // Desabilitar cache para evitar conflitos
            // Remover animações problemáticas
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        // Botão para mostrar/esconder formulário
        btnToggleForm.setOnClickListener {
            toggleFormulario()
        }

        // Botão adicionar
        btnAdicionar.setOnClickListener {
            adicionarPrescricao()
        }

        // Botão cancelar
        btnCancelar.setOnClickListener {
            esconderFormulario()
        }

        // SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchTerm = newText?.trim() ?: ""
                if (searchTerm != currentSearchTerm) {
                    currentSearchTerm = searchTerm
                    filtrarPrescricoes(searchTerm)
                }
                return true
            }
        })
    }

    private fun observeData() {
        filtrarPrescricoes(currentSearchTerm)
    }

    private fun adicionarPrescricao() {
        try {
            // Validar campos obrigatórios
            val doenca = etDoenca.text?.toString()?.trim()
            val medicamento = etMedicamento.text?.toString()?.trim()
            val dose = etDose.text?.toString()?.trim()
            val posologia = etPosologia.text?.toString()?.trim()
            val via = etVia.text?.toString()?.trim()
            val observacoes = etObservacoes.text?.toString()?.trim()

            if (doenca.isNullOrEmpty()) {
                etDoenca.error = "Campo obrigatório"
                etDoenca.requestFocus()
                return
            }

            if (medicamento.isNullOrEmpty()) {
                etMedicamento.error = "Campo obrigatório"
                etMedicamento.requestFocus()
                return
            }

            if (dose.isNullOrEmpty()) {
                etDose.error = "Campo obrigatório"
                etDose.requestFocus()
                return
            }

            if (posologia.isNullOrEmpty()) {
                etPosologia.error = "Campo obrigatório"
                etPosologia.requestFocus()
                return
            }

            if (via.isNullOrEmpty()) {
                etVia.error = "Campo obrigatório"
                etVia.requestFocus()
                return
            }

            if (observacoes.isNullOrEmpty()) {
                etObservacoes.error = "Campo obrigatório"
                etObservacoes.requestFocus()
                return
            }

            // Verificar se doença já existe
            lifecycleScope.launch {
                try {
                    val doencaExiste = repository.verificarSeDoencaExiste(doenca)

                    if (doencaExiste) {
                        // Doença existe, apenas adicionar medicamento
                        salvarPrescricao(doenca, medicamento, dose, posologia, via, observacoes)
                    } else {
                        // Perguntar se quer criar nova doença
                        showNovaDoencaDialog(doenca, medicamento, dose, posologia, via, observacoes)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@PrescricoesActivity, "Erro ao verificar doença: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@PrescricoesActivity, "Erro inesperado: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showNovaDoencaDialog(
        doenca: String,
        medicamento: String,
        dose: String,
        posologia: String,
        via: String,
        observacoes: String
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Nova Doença/Tópico")
            .setMessage("A doença/tópico \"$doenca\" não existe ainda. Deseja criar uma nova categoria?")
            .setPositiveButton("Sim, criar") { _, _ ->
                salvarPrescricao(doenca, medicamento, dose, posologia, via, observacoes)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun salvarPrescricao(
        doenca: String,
        medicamento: String,
        dose: String,
        posologia: String,
        via: String,
        observacoes: String
    ) {
        lifecycleScope.launch {
            try {
                val sucesso = repository.adicionarPrescricao(
                    doenca, medicamento, dose, posologia, via, observacoes
                )

                if (sucesso) {
                    Toast.makeText(this@PrescricoesActivity, "Prescrição adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                    limparCampos()
                    esconderFormulario()
                } else {
                    Toast.makeText(this@PrescricoesActivity, "Erro ao adicionar prescrição", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@PrescricoesActivity, "Erro inesperado: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun limparCampos() {
        etDoenca.text?.clear()
        etMedicamento.text?.clear()
        etDose.text?.clear()
        etPosologia.text?.clear()
        etVia.text?.clear()
        etObservacoes.text?.clear()
        etDoenca.requestFocus()
    }

    private fun toggleFormulario() {
        if (scrollViewForm.visibility == android.view.View.VISIBLE) {
            esconderFormulario()
        } else {
            mostrarFormulario()
        }
    }

    private fun mostrarFormulario() {
        scrollViewForm.visibility = android.view.View.VISIBLE
        btnToggleForm.text = "- Esconder Formulário"
        etDoenca.requestFocus()
    }

    private fun esconderFormulario() {
        scrollViewForm.visibility = android.view.View.GONE
        btnToggleForm.text = "+ Adicionar Nova Prescrição"
        limparCampos()
    }

    private fun recriarAdapter() {
        // Cancelar observações anteriores
        observeJob?.cancel()

        // Criar novo adapter
        adapter = PrescricoesAdapter { medicamento ->
            showDeleteConfirmationDialog(medicamento)
        }

        // Reassociar ao RecyclerView
        recyclerView.adapter = adapter
    }

    private fun filtrarPrescricoes(termo: String) {
        // Cancelar observação anterior
        observeJob?.cancel()

        observeJob = lifecycleScope.launch {
            try {
                // Aguardar um pouco para garantir que o adapter está pronto
                kotlinx.coroutines.delay(100)

                if (termo.isEmpty()) {
                    repository.obterTodasPrescricoes().collectLatest { doencasComMedicamentos ->
                        runOnUiThread {
                            try {
                                adapter.submitList(doencasComMedicamentos)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Se der erro, recriar adapter
                                recriarAdapter()
                                adapter.submitList(doencasComMedicamentos)
                            }
                        }
                    }
                } else {
                    repository.buscarPrescricoes(termo).collectLatest { doencasComMedicamentos ->
                        runOnUiThread {
                            try {
                                adapter.submitList(doencasComMedicamentos)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Se der erro, recriar adapter
                                recriarAdapter()
                                adapter.submitList(doencasComMedicamentos)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@PrescricoesActivity, "Erro ao carregar prescrições: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(medicamento: Medicamento) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Deletar Medicamento")
            .setMessage("Tem certeza que deseja deletar \"${medicamento.nome}\"?")
            .setPositiveButton("Deletar") { _, _ ->
                deletarMedicamento(medicamento)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deletarMedicamento(medicamento: Medicamento) {
        lifecycleScope.launch {
            try {
                // Mostrar loading
                runOnUiThread {
                    recyclerView.adapter = null // Remove adapter temporariamente
                }

                val sucesso = repository.deletarMedicamento(medicamento)

                runOnUiThread {
                    if (sucesso) {
                        Toast.makeText(this@PrescricoesActivity, "Medicamento deletado", Toast.LENGTH_SHORT).show()
                        // Recriar adapter completamente
                        recriarAdapter()
                        // Forçar atualização da lista
                        filtrarPrescricoes(currentSearchTerm)
                    } else {
                        Toast.makeText(this@PrescricoesActivity, "Erro ao deletar medicamento", Toast.LENGTH_SHORT).show()
                        // Restaurar adapter
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@PrescricoesActivity, "Erro inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                    // Restaurar adapter
                    recyclerView.adapter = adapter
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        observeJob?.cancel()
    }
}