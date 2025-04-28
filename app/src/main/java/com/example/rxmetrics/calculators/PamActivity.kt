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

class PamActivity : AppCompatActivity() {

    private lateinit var pasEditText: TextInputEditText
    private lateinit var padEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var pamValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pam)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }

        pasEditText = findViewById(R.id.etPas)
        padEditText = findViewById(R.id.etPad)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        pamValueTextView = findViewById(R.id.tvPamValue)

        calculateButton.setOnClickListener {
            calculatePAM()
        }
    }

    private fun calculatePAM() {

        val pasStr = pasEditText.text.toString()
        val padStr = padEditText.text.toString()

        if (pasStr.isEmpty() || padStr.isEmpty()) {
            if (pasStr.isEmpty()) {
                pasEditText.error = "Por favor, insira a pressão arterial sistólica"
            }
            if (padStr.isEmpty()) {
                padEditText.error = "Por favor, insira a pressão arterial diastólica"
            }
            return
        }

        try {
            val pas = pasStr.toFloat()
            val pad = padStr.toFloat()

            if (pas <= 0 || pad <= 0) {
                if (pas <= 0){
                    pasEditText.error = "A pressão sistólica deve ser maior que zero"
                }
                if (pad <= 0){
                    padEditText.error = "A pressão diastólica deve ser maior que zero"
                }
                return
            }

            val pam = (pas + (2 * pad)) / 3

            val formattedPam = String.format("%.0f", pam)

            pamValueTextView.text = "PAM: $formattedPam mmHg"

            resultCardView.visibility = View.VISIBLE
        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            pasEditText.error = "Formato de número inválido"
            padEditText.error = "Formato de número inválido"
        }
    }
}