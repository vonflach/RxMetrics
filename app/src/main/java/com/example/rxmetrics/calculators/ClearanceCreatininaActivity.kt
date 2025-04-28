package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rxmetrics.R
import com.example.rxmetrics.databinding.ActivityClearanceCreatininaBinding
import kotlin.math.pow

class ClearanceCreatininaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClearanceCreatininaBinding

    // Lista de métodos de cálculo diretamente no código
    private val metodosList = listOf("Cockcroft-Gault", "MDRD", "CKD-EPI (2021)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClearanceCreatininaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdown()
        setupListeners()

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }
    }

    private fun setupDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metodosList)
        (binding.spinnerMetodo as? AutoCompleteTextView)?.setAdapter(adapter)

        // Defina o método padrão como Cockcroft-Gault
        binding.spinnerMetodo.setText(metodosList[0], false)
    }

    private fun setupListeners() {
        binding.spinnerMetodo.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> { // Cockcroft-Gault
                    binding.tilPeso.visibility = View.VISIBLE
                    binding.layoutEtnia.visibility = View.GONE
                }
                1 -> { // MDRD
                    binding.tilPeso.visibility = View.GONE
                    binding.layoutEtnia.visibility = View.VISIBLE
                }
                2 -> { // CKD-EPI
                    binding.tilPeso.visibility = View.GONE
                    binding.layoutEtnia.visibility = View.GONE
                }
            }
        }

        binding.btnCalcular.setOnClickListener {
            calcularClearance()
        }
    }

    private fun calcularClearance() {
        try {
            val idade = binding.etIdade.text.toString().toDoubleOrNull()
            val creatinina = binding.etCreatinina.text.toString().toDoubleOrNull()

            if (idade == null || creatinina == null) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                return
            }

            val isMasculino = binding.rbMasculino.isChecked
            val isNegro = binding.rbNegro.isChecked

            var resultado = 0.0

            when (binding.spinnerMetodo.text.toString()) {
                metodosList[0] -> { // Cockcroft-Gault
                    val peso = binding.etPeso.text.toString().toDoubleOrNull()
                    if (peso == null) {
                        Toast.makeText(this, "Peso é obrigatório para Cockcroft-Gault", Toast.LENGTH_SHORT).show()
                        return
                    }
                    resultado = calcularCockcroft(idade, peso, creatinina, isMasculino)
                }
                metodosList[1] -> { // MDRD
                    resultado = calcularMDRD(idade, creatinina, isMasculino, isNegro)
                }
                metodosList[2] -> { // CKD-EPI (2021)
                    resultado = calcularCKDEPI(idade, creatinina, isMasculino)
                }
            }

            val resultadoFormatado = String.format("%.2f ml/min/1,73m²", resultado)
            binding.tvClearanceResult.text = resultadoFormatado

            val classificacao = classificarClearance(resultado)
            binding.tvClassificacao.text = classificacao

            binding.cvResultado.visibility = View.VISIBLE

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao calcular. Verifique os valores inseridos.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calcularCockcroft(idade: Double, peso: Double, creatinina: Double, isMasculino: Boolean): Double {
        var resultado = (140 - idade) * peso / (72 * creatinina)
        if (!isMasculino) {
            resultado *= 0.85
        }
        return resultado
    }

    private fun calcularMDRD(idade: Double, creatinina: Double, isMasculino: Boolean, isNegro: Boolean): Double {
        var resultado = 175 * creatinina.pow(-1.154) * idade.pow(-0.203)

        if (!isMasculino) {
            resultado *= 0.742
        }

        if (isNegro) {
            resultado *= 1.212
        }

        return resultado
    }

    private fun calcularCKDEPI(idade: Double, creatinina: Double, isMasculino: Boolean): Double {
        val s = if (isMasculino) 1 else 1.012
        val a = if (isMasculino) 0.9 else 0.7
        val b = if (isMasculino && creatinina <= 0.9) {
            -0.302
        } else if (creatinina <= 0.7) {
            -0.241
        } else {
            -1.2
        }

        val CrSobreA = creatinina / a
        val Id = Math.pow(0.9938, idade)

        val resultado = (142 * Id * Math.pow(CrSobreA, b)) * s.toDouble()

        return resultado
    }

    private fun classificarClearance(resultado: Double): String {
        return when {
            resultado >= 90 -> "Classificação KDIGO: G1"
            resultado >= 60 -> "Classificação KDIGO: G2"
            resultado >= 45 -> "Classificação KDIGO: G3a"
            resultado >= 30 -> "Classificação KDIGO: G3b"
            resultado >= 15 -> "Classificação KDIGO: G4"
            else -> "Classificação KDIGO: G5"
        }
    }
}