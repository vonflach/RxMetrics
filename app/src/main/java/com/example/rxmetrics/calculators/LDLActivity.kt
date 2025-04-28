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

class LDLActivity : AppCompatActivity() {

    private lateinit var totalEditText: TextInputEditText
    private lateinit var hdlEditText: TextInputEditText
    private lateinit var trigEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var ldlValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ldl)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }

        totalEditText = findViewById(R.id.etColesterolTotal)
        hdlEditText = findViewById(R.id.etHDL)
        trigEditText = findViewById(R.id.etTrig)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        ldlValueTextView = findViewById(R.id.tvLDLValue)

        calculateButton.setOnClickListener {
            calculateLDL()
        }
    }

    private fun calculateLDL() {

        val totalStr = totalEditText.text.toString()
        val hdlStr = hdlEditText.text.toString()
        val trigStr = trigEditText.text.toString()

        if (totalStr.isEmpty() || hdlStr.isEmpty() || trigStr.isEmpty()) {
            if (totalStr.isEmpty()) {
                totalEditText.error = "Por favor, insira o colesterol total"
            }
            if (hdlStr.isEmpty()) {
                hdlEditText.error = "Por favor, insira o colesterol HDL"
            }
            if (trigStr.isEmpty()) {
                trigEditText.error = "Por favor, insira o triglicerídeos"
            }
            return
        }

        try {
            val total = totalStr.toFloat()
            val hdl = hdlStr.toFloat()
            val trig = trigStr.toFloat()

            if (total <= 0 || hdl <= 0 || trig <= 0) {
                if (total <= 0) {
                    totalEditText.error = "O colesterol total deve ser maior que zero"
                }
                if (hdl <= 0) {
                    hdlEditText.error = "O colesterol HDL deve ser maior que zero"
                }
                if (trig <= 0) {
                    trigEditText.error = "O triglicerídeos total deve ser maior que zero"
                }
                return
            }

            val ldl = total - (hdl + (trig/5))

            val formattedLdl = String.format("%.1f", ldl)

            ldlValueTextView.text = "Colesterol LDL: $formattedLdl mg/dL"

            resultCardView.visibility = View.VISIBLE

        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            totalEditText.error = "Formato de número inválido"
            hdlEditText.error = "Formato de número inválido"
            trigEditText.error = "Formato de número inválido"
        }
    }
}