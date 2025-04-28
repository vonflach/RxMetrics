package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.pow

class BMICalculatorActivity : AppCompatActivity() {

    private lateinit var weightEditText: TextInputEditText
    private lateinit var heightEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var bmiValueTextView: TextView
    private lateinit var bmiCategoryTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_calculator)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }


        // Habilitar o botão voltar na ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calculadora de IMC"

        // Inicializar as views
        weightEditText = findViewById(R.id.etWeight)
        heightEditText = findViewById(R.id.etHeight)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        bmiValueTextView = findViewById(R.id.tvBmiValue)
        bmiCategoryTextView = findViewById(R.id.tvBmiCategory)

        // Configurar o botão de calcular
        calculateButton.setOnClickListener {
            calculateBMI()
        }
    }

    private fun calculateBMI() {
        val weightStr = weightEditText.text.toString()
        val heightStr = heightEditText.text.toString()

        // Verificar se os campos estão preenchidos
        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            // Mostrar mensagem de erro
            if (weightStr.isEmpty()) {
                weightEditText.error = "Por favor, insira o peso"
            }
            if (heightStr.isEmpty()) {
                heightEditText.error = "Por favor, insira a altura"
            }
            return
        }

        try {
            // Converter input para números
            val weight = weightStr.toFloat()
            val height = heightStr.toFloat()

            // Verificar se os valores são válidos
            if (weight <= 0 || height <= 0) {
                if (weight <= 0) {
                    weightEditText.error = "O peso deve ser maior que zero"
                }
                if (height <= 0) {
                    heightEditText.error = "A altura deve ser maior que zero"
                }
                return
            }

            // Calcular o IMC: IMC = peso / (altura²)
            val bmi = weight / height.pow(2)

            // Formatar o valor do IMC para uma casa decimal
            val formattedBmi = String.format("%.1f", bmi)

            // Mostrar o resultado
            bmiValueTextView.text = "IMC: $formattedBmi kg/m²"

            // Determinar a categoria do IMC
            val category = interpretBMI(bmi)
            bmiCategoryTextView.text = "Classificação: $category"

            // Mostrar o card de resultado
            resultCardView.visibility = View.VISIBLE

        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            weightEditText.error = "Formato de número inválido"
            heightEditText.error = "Formato de número inválido"
        }
    }

    private fun interpretBMI(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Baixo peso"
            bmi < 25 -> "Peso normal"
            bmi < 30 -> "Sobrepeso"
            bmi < 35 -> "Obesidade Grau I"
            bmi < 40 -> "Obesidade Grau II"
            else -> "Obesidade Grau III"
        }
    }



    // Tratar o botão de voltar na ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}