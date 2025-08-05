package com.example.rxmetrics

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxmetrics.calculators.*
import com.google.android.material.card.MaterialCardView
import java.text.Collator
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var suggestionsRecyclerView: RecyclerView
    private lateinit var searchCard: MaterialCardView
    private lateinit var ivInfo: ImageView

    // Adapters
    private lateinit var calculatorAdapter: CalculatorAdapter
    private lateinit var suggestionAdapter: SuggestionAdapter

    // Data Collections
    private val calculatorsList = mutableListOf<Calculator>()
    private val displayList = mutableListOf<Calculator>()
    private val suggestionsList = mutableListOf<Calculator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerViews()
        prepareCalculatorData()
        setupAdapters()
        setupSearch()
        setupInfoIcon()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rvCalculators)
        searchView = findViewById(R.id.searchView)
        suggestionsRecyclerView = findViewById(R.id.rvSuggestions)
        searchCard = findViewById(R.id.cvSearchBar)
        ivInfo = findViewById(R.id.ivInfo)

        val searchEditText = searchView.findViewById<android.widget.EditText>(
            androidx.appcompat.R.id.search_src_text
        )

        // Força o hint (opcional se já está no XML)
        searchEditText.hint = "Buscar..."

        // Define a cor do hint (pode ser qualquer cor visível no seu tema)
        searchEditText.setHintTextColor(getColor(R.color.textSecondary))
    }

    private fun setupRecyclerViews() {
        // Configurar RecyclerView para calculadoras
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar RecyclerView para sugestões
        suggestionsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAdapters() {
        // Copiar todas as calculadoras para a lista de exibição
        displayList.addAll(calculatorsList)

        // Configurar o adapter para calculadoras
        calculatorAdapter = CalculatorAdapter(displayList) { calculator ->
            navigateToCalculator(calculator)
        }
        recyclerView.adapter = calculatorAdapter

        // Configurar o adapter para sugestões
        suggestionAdapter = SuggestionAdapter(suggestionsList) { suggestion ->
            handleSuggestionClick(suggestion)
        }
        suggestionsRecyclerView.adapter = suggestionAdapter
    }

    private fun setupSearch() {
        // Fazer toda a área do card ser clicável
        searchCard.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                suggestionsRecyclerView.visibility = View.GONE
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearchTextChange(newText)
                return true
            }
        })

        // Fechar sugestões quando a barra de busca perder o foco
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                suggestionsRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun handleSearchTextChange(newText: String?) {
        // Mostrar/esconder sugestões baseado no texto
        if (!newText.isNullOrEmpty()) {
            updateSuggestions(newText)
            suggestionsRecyclerView.visibility = View.VISIBLE
        } else {
            suggestionsRecyclerView.visibility = View.GONE
        }

        // Filtrar a lista principal
        updateMainList(newText)
    }

    private fun prepareCalculatorData() {
        // Adicionar calculadoras à lista com dados estruturados
        val data = listOf(
            Calculator(1, "IMC - Índice de Massa Corporal",
                "Calcule o IMC baseado no peso e altura do paciente",
                CalculatorType.BMI),
            Calculator(2, "Calculadora de Dose",
                "Calcule a dose de infusão contínua utilizada",
                CalculatorType.DOSE_CALC),
            Calculator(3, "Clearance de Creatinina",
                "Cálculo da função renal baseado na creatinina sérica",
                CalculatorType.CREATININE_CLEARANCE),
            Calculator(4, "Peso Ideal",
                "Calcule o peso ideal a partir da altura do paciente",
                CalculatorType.PESO_IDEAL),
            Calculator(5, "Sódio corrigido",
                "Determine o sódio corrigido pela glicemia do paciente utilizando a fórmula de Hillier",
                CalculatorType.SODIO_GLICEMIA),
            Calculator(6, "Osmolaridade Plasmática",
                "Encontre a osmolaridade sérica a partir dos principais solutos do organismo",
                CalculatorType.OSMO_SERICA),
            Calculator(7, "PAM - Pressão Arterial Média",
                "Calcule a PAM a partir da pressão sistólica e diastólica",
                CalculatorType.PAM),
            Calculator(8, "Cálcio corrigido",
                "Encontre o cálcio corrigido pelo nível de albumina sérica",
                CalculatorType.CALCIO_ALBUMINA),
            Calculator(9, "Escore de Wells para TVP",
                "Calcule o risco de Trombose Venosa Profunda a partir dos critérios de Wells",
                CalculatorType.WELLS_TVP),
            Calculator(10, "Infusão de fluído intraoperatória",
                "Encontre a quantidade adequada de fluído para administração durante o ato cirúrgico",
                CalculatorType.FLUIDO_INTRA_OP),
            Calculator(11, "Calculadora para Colesterol LDL",
                "Encontre o nível de colesterol LDL utilizando a fórmula de Friedwald",
                CalculatorType.LDL),
            Calculator(12, "Escore de Wells para TEP",
                "Encontre o risco de Tromboembolismo Pulmonar pelos critérios de Wells",
                CalculatorType.WELLS_TEP),
            Calculator(13, "Déficit de Potássio",
                "Encontre a quantidade faltante de potássio sérico",
                CalculatorType.DEFICIT_K),
            Calculator(14, "Correção de Sódio para Hiper e Hiponatremia",
                "Calcule a taxa de infusão para a correção do sódio sérico",
                CalculatorType.CORRECAO_NA),
            Calculator(15, "Prescrições Rápidas",
                "Gerencie suas prescrições médicas favoritas e busque rapidamente",
                CalculatorType.PRESCRICOES_RAPIDAS)
        )

        calculatorsList.addAll(data)

        // Ordenar calculadoras por nome em ordem alfabética com suporte a caracteres especiais
        val collator = Collator.getInstance(Locale("pt", "BR"))
        collator.strength = Collator.PRIMARY
        calculatorsList.sortWith(compareBy(collator) { it.name })
    }

    private fun updateSuggestions(query: String) {
        val searchText = query.lowercase()
        val filteredList = calculatorsList.filter { calculator ->
            calculator.name.lowercase().contains(searchText) ||
                    calculator.description.lowercase().contains(searchText)
        }

        // Limitar para as 5 primeiras sugestões
        val limitedSuggestions = filteredList.take(5)
        suggestionAdapter.updateSuggestions(limitedSuggestions)
    }

    private fun updateMainList(query: String?) {
        displayList.clear()

        if (query.isNullOrEmpty()) {
            displayList.addAll(calculatorsList)
        } else {
            val searchText = query.lowercase()
            val filtered = calculatorsList.filter { calculator ->
                calculator.name.lowercase().contains(searchText) ||
                        calculator.description.lowercase().contains(searchText)
            }
            displayList.addAll(filtered)
        }

        calculatorAdapter.notifyDataSetChanged()
    }

    private fun handleSuggestionClick(suggestion: Calculator) {
        // Preencher a barra de busca com o texto da sugestão
        searchView.setQuery(suggestion.name, false)

        // Esconder as sugestões
        suggestionsRecyclerView.visibility = View.GONE

        // Navegar para a calculadora selecionada
        navigateToCalculator(suggestion)
    }

    private fun navigateToCalculator(calculator: Calculator) {
        val activityClass = when (calculator.type) {
            CalculatorType.BMI -> BMICalculatorActivity::class.java
            CalculatorType.DOSE_CALC -> DoseCalculatorActivity::class.java
            CalculatorType.CREATININE_CLEARANCE -> ClearanceCreatininaActivity::class.java
            CalculatorType.PESO_IDEAL -> PesoIdealActivity::class.java
            CalculatorType.OSMO_SERICA -> OsmoActivity::class.java
            CalculatorType.PAM -> PamActivity::class.java
            CalculatorType.SODIO_GLICEMIA -> SodioGlicemiaActivity::class.java
            CalculatorType.CALCIO_ALBUMINA -> CalcioAlbuminaActivity::class.java
            CalculatorType.WELLS_TVP -> WellsTvpActivity::class.java
            CalculatorType.FLUIDO_INTRA_OP -> FluidoIntraOpActivity::class.java
            CalculatorType.LDL -> LDLActivity::class.java
            CalculatorType.WELLS_TEP -> WellsTepActivity::class.java
            CalculatorType.DEFICIT_K -> DeficitKActivity::class.java
            CalculatorType.CORRECAO_NA -> CorreçãoNaActivity::class.java
            CalculatorType.PRESCRICOES_RAPIDAS -> PrescricoesActivity::class.java
            else -> null
        }

        if (activityClass != null) {
            startActivity(Intent(this, activityClass))
        } else {
            Toast.makeText(this, "Funcionalidade em desenvolvimento: ${calculator.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupInfoIcon() {
        ivInfo.setOnClickListener { showInfoDialog() }
    }

    private fun showInfoDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme)

        // Criar layout personalizado para o dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)

        // Configurar o dialog
        builder.setView(dialogView)

        val dialog = builder.create()

        // Configurar botão de fechar no dialog
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        btnClose.setOnClickListener { dialog.dismiss() }

        // Configurar para dialog ser cancelável tocando fora
        dialog.setCanceledOnTouchOutside(true)

        // Mostrar dialog
        dialog.show()

        // Ajustar tamanho do dialog para ser responsivo
        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.attributes.windowAnimations = R.style.DialogAnimation

            // Configurar largura responsiva
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.9).toInt() // 90% da largura da tela
            val maxWidth = (400 * displayMetrics.density).toInt() // máximo 400dp

            window.setLayout(
                minOf(width, maxWidth),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}