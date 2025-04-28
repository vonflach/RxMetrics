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

class SodioGlicemiaActivity : AppCompatActivity() {

    private lateinit var sodioEditText: TextInputEditText
    private lateinit var glicoseEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var sodcValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sodio_glicemia)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }

        sodioEditText = findViewById(R.id.etSodio)
        glicoseEditText = findViewById(R.id.etGlicemia)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        sodcValueTextView = findViewById(R.id.tvSodcValue)

        calculateButton.setOnClickListener {
            calculateSodc()
        }
    }

    private fun calculateSodc() {

        val sodStr = sodioEditText.text.toString()
        val glicoseStr = glicoseEditText.text.toString()

        // Verificar se Empty
        if (sodStr.isEmpty() || glicoseStr.isEmpty()) {
            if (sodStr.isEmpty()) {
                sodioEditText.error = "Por favor, insira o sódio sérico"
            }
            if (glicoseStr.isEmpty()) {
                glicoseEditText.error = "Por favor, insira a glicose sérica"
            }
            return
        }

        try {
            val sod = sodStr.toFloat()
            val glicose = glicoseStr.toFloat()

            // Verificar se valor válido
            if (sod <= 0 || glicose <= 0) {
                if (sod <= 0) {
                    sodioEditText.error = "O sódio sérico deve ser maior que zero"
                }
                if (glicose <= 0) {
                    glicoseEditText.error = "A glicose sérica deve ser maior que zero"
                }
                return
            }

            val SodC = sod + (0.024 * (glicose - 100))

            val formattedSodC = String.format("%.1f", SodC)

            sodcValueTextView.text = "Sódio corrigido: $formattedSodC mEq/L"

            resultCardView.visibility = View.VISIBLE
        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            sodioEditText.error = "Formato de número inválido"
            glicoseEditText.error = "Formato de número inválido"
        }
    }
}