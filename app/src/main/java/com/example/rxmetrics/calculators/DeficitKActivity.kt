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

class DeficitKActivity : AppCompatActivity() {

    private lateinit var pesoEditText: TextInputEditText
    private lateinit var kAtualEditText: TextInputEditText
    private lateinit var kDesejadoEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var deficitValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_k_deficit)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }

        pesoEditText = findViewById(R.id.etKPeso)
        kAtualEditText = findViewById(R.id.etKAtual)
        kDesejadoEditText = findViewById(R.id.etKDesejado)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        deficitValueTextView = findViewById(R.id.tvDeficitValue)

        calculateButton.setOnClickListener {
            calculateDeficit()
        }
    }

    private fun calculateDeficit() {

        val pesoStr = pesoEditText.text.toString()
        val kAtualStr = kAtualEditText.text.toString()
        val kDesejadoStr = kDesejadoEditText.text.toString()

        if (pesoStr.isEmpty() || kAtualStr.isEmpty() || kDesejadoStr.isEmpty()) {
            if (pesoStr.isEmpty()) {
                pesoEditText.error = "Por favor, insira o peso"
            }
            if (kAtualStr.isEmpty()) {
                kAtualEditText.error = "Por favor, insira o potássio atual"
            }
            if (kDesejadoStr.isEmpty()) {
                kDesejadoEditText.error = "Por favor, insira o potássio alvo"
            }
            return
        }

        try {
            val peso = pesoStr.toFloat()
            val katual = kAtualStr.toFloat()
            val kalvo = kDesejadoStr.toFloat()

            if (peso <= 0 || katual <= 0 || kalvo <= 0) {
                if (peso <= 0) {
                    pesoEditText.error = "O peso deve ser maior que zero"
                }
                if (katual <= 0) {
                    kAtualEditText.error = "O potássio atual deve ser maior que zero"
                }
                if (kalvo <= 0) {
                    kDesejadoEditText.error = "O potássio alvo deve ser maior que zero"
                }
                return
            }

            val deficit = (kalvo-katual)*peso*0.4

            val formattedDeficit = String.format("%.2f", deficit)

            deficitValueTextView.text = "Déficit de K+: $formattedDeficit mEq/L"

            resultCardView.visibility = View.VISIBLE

        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            pesoEditText.error = "Formato de número inválido"
            kAtualEditText.error = "Formato de número inválido"
            kDesejadoEditText.error = "Formato de número inválido"
        }
    }
}