package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R
import com.google.android.material.textfield.TextInputEditText

class FluidoIntraOpActivity : AppCompatActivity() {
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var resultTextView: TextView
    private lateinit var etPeso: TextInputEditText
    private lateinit var etJejum: TextInputEditText
    private lateinit var dropdownTrauma: AutoCompleteTextView
    private lateinit var scrollView: ScrollView

    // Variável para armazenar a seleção de gravidade
    private var gravidadeTrauma: String = "Mínima"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fluido_intra_op)

        // Inicializando componentes
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        resultTextView = findViewById(R.id.tvFluidoValue)
        etPeso = findViewById(R.id.etPeso)
        etJejum = findViewById(R.id.etJejum)
        dropdownTrauma = findViewById(R.id.dropdownTrauma)
        scrollView = findViewById(R.id.scrollView)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Retorna à tela anterior
        }

        // Configurando o dropdown
        setupTraumaDropdown()

        calculateButton.setOnClickListener {
            calculateFluidoIntraOp()

            // Scroll automático para o resultado
            scrollView.postDelayed({
                scrollView.smoothScrollTo(0, resultCardView.bottom)
            }, 200)
        }
    }

    private fun setupTraumaDropdown() {
        val items = listOf("Mínima", "Moderada", "Severa")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        dropdownTrauma.setAdapter(adapter)

        // Definir o valor inicial
        dropdownTrauma.setText("Mínima", false)

        // Configurar o listener para detectar mudanças na seleção
        dropdownTrauma.setOnItemClickListener { _, _, position, _ ->
            gravidadeTrauma = items[position]
        }
    }

    private fun calculateFluidoIntraOp() {
        // Verificar se os campos estão preenchidos
        val pesoStr = etPeso.text.toString()
        val jejumStr = etJejum.text.toString()

        if (pesoStr.isEmpty() || jejumStr.isEmpty()) {
            if (pesoStr.isEmpty()) {
                etPeso.error = "Por favor, insira o peso"
            }
            if (jejumStr.isEmpty()) {
                etJejum.error = "Por favor, insira o tempo de jejum"
            }
            return
        }

        try {
            val peso = pesoStr.toFloat()
            val jejum = jejumStr.toFloat()

            // Validação de valores
            if (peso <= 0) {
                etPeso.error = "O peso deve ser maior que zero"
                return
            }
            if (jejum < 0) {
                etJejum.error = "O tempo de jejum deve ser no mínimo zero"
                return
            }

            // Cálculos
            val resultados = calcularFluidos(peso, jejum, gravidadeTrauma)
            exibirResultados(resultados)

        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            Toast.makeText(this, "Formato de número inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calcularFluidos(peso: Float, jejum: Float, gravidade: String): Map<String, Float> {
        val manutencaoHr = peso + 40
        val deficitJejum = manutencaoHr * jejum

        val fatorGravidade = when (gravidade) {
            "Mínima" -> 3f
            "Moderada" -> 5f
            "Severa" -> 7f
            else -> 3f // Valor padrão caso ocorra algum erro
        }

        val perdaCirurgica = peso * fatorGravidade

        return mapOf(
            "hora1" to ((deficitJejum / 2) + manutencaoHr),
            "hora2" to ((deficitJejum / 4) + manutencaoHr + perdaCirurgica),
            "hora3" to ((deficitJejum / 4) + manutencaoHr + perdaCirurgica),
            "hora4+" to (manutencaoHr + perdaCirurgica)
        )
    }

    private fun exibirResultados(resultados: Map<String, Float>) {
        val texto = StringBuilder()

        texto.append("Fluído 1ª hora: ${String.format("%.0f", resultados["hora1"])} mL/hr\n")
        texto.append("Fluído 2ª hora: ${String.format("%.0f", resultados["hora2"])} mL/hr\n")
        texto.append("Fluído 3ª hora: ${String.format("%.0f", resultados["hora3"])} mL/hr\n")
        texto.append("Fluído 4ª hora em diante: ${String.format("%.0f", resultados["hora4+"])} mL/hr")

        resultTextView.text = texto.toString()
        resultCardView.visibility = View.VISIBLE
    }
}