package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DoseCalculatorActivity : AppCompatActivity() {

    private lateinit var pesoEditText: TextInputEditText
    private lateinit var medEditText: TextInputEditText
    private lateinit var solEditText: TextInputEditText
    private lateinit var vazaoEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var doseValueTextView: TextView
    private lateinit var unidadeRadioGroup: RadioGroup
    private lateinit var considerarPesoRadioGroup: RadioGroup
    private lateinit var tempoRadioGroup: RadioGroup
    private lateinit var tilWeight: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dose_calculator)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Habilitar o botão voltar na ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calculadora de Dose"

        // Inicializar as views
        pesoEditText = findViewById(R.id.etWeight)
        vazaoEditText = findViewById(R.id.etVazao)
        medEditText = findViewById(R.id.etMed)
        solEditText = findViewById(R.id.etSol)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        doseValueTextView = findViewById(R.id.tvDoseValue)
        unidadeRadioGroup = findViewById(R.id.rgUnidade)
        considerarPesoRadioGroup = findViewById(R.id.rgConsiderarPeso)
        tempoRadioGroup = findViewById(R.id.rgTempo)
        tilWeight = findViewById(R.id.tilWeight)

        // Configurar listener para a escolha de considerar peso ou não
        considerarPesoRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPesoSim -> {
                    tilWeight.visibility = View.VISIBLE
                }
                R.id.rbPesoNao -> {
                    tilWeight.visibility = View.GONE
                }
            }
        }

        // Configurar o botão de calcular
        calculateButton.setOnClickListener {
            calculateDose()
        }
    }

    private fun calculateDose() {
        // Verificar se peso deve ser considerado
        val considerarPeso = findViewById<RadioButton>(R.id.rbPesoSim).isChecked

        // Variáveis para os valores de entrada
        val vazaoStr = vazaoEditText.text.toString()
        val medStr = medEditText.text.toString()
        val solStr = solEditText.text.toString()

        // Lista de campos para validação
        val camposObrigatorios = mutableListOf(
            Pair(vazaoStr, vazaoEditText),
            Pair(medStr, medEditText),
            Pair(solStr, solEditText)
        )

        // Adicionar peso à lista apenas se for considerado
        val pesoStr = pesoEditText.text.toString()
        if (considerarPeso) {
            camposObrigatorios.add(Pair(pesoStr, pesoEditText))
        }

        // Verificar campos vazios
        var campoVazio = false
        for ((valor, campo) in camposObrigatorios) {
            if (valor.isEmpty()) {
                campo.error = "Este campo é obrigatório"
                campoVazio = true
            }
        }

        if (campoVazio) return

        try {
            // Converter input para números
            val vazao = vazaoStr.toFloat()
            val med = medStr.toFloat()
            val sol = solStr.toFloat()

            // Verificar valores inválidos
            var valorInvalido = false
            if (vazao <= 0) {
                vazaoEditText.error = "A vazão deve ser maior que zero"
                valorInvalido = true
            }
            if (med <= 0) {
                medEditText.error = "A quantidade de medicamento deve ser maior que zero"
                valorInvalido = true
            }
            if (sol <= 0) {
                solEditText.error = "A quantidade de solução deve ser maior que zero"
                valorInvalido = true
            }

            if (valorInvalido) return

            // Cálculo considerando o peso
            var dose: Float
            if (considerarPeso) {
                val peso = pesoStr.toFloat()
                if (peso <= 0) {
                    pesoEditText.error = "O peso deve ser maior que zero"
                    return
                }
                // Fórmula: dose = (vazao * med) / (60 * peso * sol)
                dose = (vazao * med) / (60 * peso * sol)
            } else {
                // Cálculo sem considerar peso
                dose = (vazao * med) / (60 * sol)
            }

            // Verificar a unidade de medicamento escolhida
            val usarMG = findViewById<RadioButton>(R.id.rbMG).isChecked
            val usarMcg = findViewById<RadioButton>(R.id.rbMCG).isChecked
            val usarUI = findViewById<RadioButton>(R.id.rbUI).isChecked

            // Verificar a unidade de tempo escolhida
            val usarMin = findViewById<RadioButton>(R.id.rbMin).isChecked
            val usarHr = findViewById<RadioButton>(R.id.rbHr).isChecked

            // Ajustar dose conforme a unidade de medicamento
            when {
                usarMcg -> {
                    // Converter de mg para mcg (multiplicar por 1000)
                    dose *= 1000
                }
                usarUI -> {
                    // Para UI, manter o valor como está (assumindo que a medicação já está em UI)
                    // Se necessário, pode aplicar conversões específicas aqui
                }
                // Para mg, manter como está
            }

            // Ajustar dose conforme a unidade de tempo
            if (usarHr) {
                // Converter de /min para /hr (multiplicar por 60)
                dose *= 60
            }

            // Determinar o texto da unidade para exibição
            val unidadeTexto = when {
                usarMG -> "mg"
                usarMcg -> "mcg"
                usarUI -> "UI"
                else -> "mg" // default
            }

            val tempoTexto = if (usarHr) "hr" else "min"
            val sufixoUnidade = if (considerarPeso) "/kg/$tempoTexto" else "/$tempoTexto"

            // Formatar o resultado
            val formattedDose = when {
                usarUI -> String.format("%.2f", dose) // UI geralmente não usa decimais
                dose < 1 -> String.format("%.2f", dose) // Mais precisão para valores pequenos
                else -> String.format("%.2f", dose)
            }

            // Mostrar o resultado
            doseValueTextView.text = "Dose: $formattedDose $unidadeTexto$sufixoUnidade"

            // Mostrar o card de resultado
            resultCardView.visibility = View.VISIBLE

        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            vazaoEditText.error = "Formato de número inválido"
            medEditText.error = "Formato de número inválido"
            solEditText.error = "Formato de número inválido"
            if (considerarPeso) {
                pesoEditText.error = "Formato de número inválido"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}