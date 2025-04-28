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

class CalcioAlbuminaActivity : AppCompatActivity() {

    private lateinit var calcioEditText: TextInputEditText
    private lateinit var albuminaEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var cacValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcio_albumina)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }

        calcioEditText = findViewById(R.id.etCalcio)
        albuminaEditText = findViewById(R.id.etAlbumina)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        cacValueTextView = findViewById(R.id.tvCaCValue)

        calculateButton.setOnClickListener {
            calculateCac()
        }
    }

    private fun calculateCac() {

        val caStr = calcioEditText.text.toString()
        val albStr = albuminaEditText.text.toString()

        // Verificar se Empty
        if (caStr.isEmpty() || albStr.isEmpty()) {
            if (caStr.isEmpty()) {
                calcioEditText.error = "Por favor, insira o cálcio sérico"
            }
            if (albStr.isEmpty()) {
                albuminaEditText.error = "Por favor, insira a albumina sérica"
            }
            return
        }

        try {
            val ca = caStr.toFloat()
            val alb = albStr.toFloat()

            // Verificar se valor válido
            if (ca <= 0 || alb <= 0) {
                if (ca <= 0) {
                    calcioEditText.error = "O cálcio sérico deve ser maior que zero"
                }
                if (alb <= 0) {
                    albuminaEditText.error = "A albumina sérica deve ser maior que zero"
                }
                return
            }

            val cac = (0.8 * (4 - alb)) + ca

            val formattedCaC = String.format("%.1f", cac)

            cacValueTextView.text = "Cálcio corrigido: $formattedCaC mg/dL"

            resultCardView.visibility = View.VISIBLE
        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            calcioEditText.error = "Formato de número inválido"
            albuminaEditText.error = "Formato de número inválido"
        }
    }
}