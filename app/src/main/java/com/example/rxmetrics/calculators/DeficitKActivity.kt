package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.pow
import kotlin.math.roundToInt

class DeficitKActivity : AppCompatActivity() {

    private lateinit var pesoEditText: TextInputEditText
    private lateinit var kAtualEditText: TextInputEditText
    private lateinit var kDesejadoEditText: TextInputEditText
    private lateinit var velInfuEditText: TextInputEditText
    private lateinit var volumeEditText: TextInputEditText
    private lateinit var apresentaçãoRadioGroup: RadioGroup
    private lateinit var acessoRadioGroup: RadioGroup
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var deficitValueTextView: TextView
    private lateinit var volAspirarTextView: TextView
    private lateinit var velocidadeTextView: TextView
    private lateinit var tempoInfusaoTextView: TextView
    private lateinit var scrollView: androidx.core.widget.NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_k_deficit)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        pesoEditText = findViewById(R.id.etKPeso)
        kAtualEditText = findViewById(R.id.etKAtual)
        kDesejadoEditText = findViewById(R.id.etKDesejado)
        velInfuEditText = findViewById(R.id.etVelK)
        volumeEditText = findViewById(R.id.etVolume)
        apresentaçãoRadioGroup = findViewById(R.id.rgApresentação)
        acessoRadioGroup = findViewById(R.id.rgAcesso)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        deficitValueTextView = findViewById(R.id.tvDeficitValue)
        volAspirarTextView = findViewById(R.id.tvVolAspirarValue)
        velocidadeTextView = findViewById(R.id.tvVelocidadeValue)
        tempoInfusaoTextView = findViewById(R.id.tvTempoInfusaoValue)
        scrollView = findViewById(R.id.scrollView)

        calculateButton.setOnClickListener {
            calculateDeficit()
        }
    }

    private fun calculateDeficit() {
        val pesoStr = pesoEditText.text.toString()
        val kAtualStr = kAtualEditText.text.toString()
        val kDesejadoStr = kDesejadoEditText.text.toString()
        val velInfuStr = velInfuEditText.text.toString()
        val volumeStr = volumeEditText.text.toString()
        val selectedIdApresentaçao = apresentaçãoRadioGroup.checkedRadioButtonId
        val selectedIdAcesso = acessoRadioGroup.checkedRadioButtonId

        // Validação de campos vazios
        if (pesoStr.isEmpty() || kAtualStr.isEmpty() || kDesejadoStr.isEmpty() || velInfuStr.isEmpty() || volumeStr.isEmpty()) {
            if (pesoStr.isEmpty()) {
                pesoEditText.error = "Por favor, insira o peso"
            }
            if (kAtualStr.isEmpty()) {
                kAtualEditText.error = "Por favor, insira o potássio atual"
            }
            if (kDesejadoStr.isEmpty()) {
                kDesejadoEditText.error = "Por favor, insira o potássio alvo"
            }
            if (velInfuStr.isEmpty()) {
                velInfuEditText.error = "Por favor, insira a velocidade de infusão"
            }
            if (volumeStr.isEmpty()) {
                volumeEditText.error = "Por favor, insira o volume a ser infundido"
            }
            return
        }

        if (selectedIdAcesso == -1) {
            Toast.makeText(this, "Por favor, selecione a via de acesso", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedIdApresentaçao == -1) {
            Toast.makeText(this, "Por favor, selecione a apresentação", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val peso = pesoStr.toFloat()
            val katual = kAtualStr.toFloat()
            val kalvo = kDesejadoStr.toFloat()
            val velInfu = velInfuStr.toFloat()
            val volume = volumeStr.toFloat()

            // Validação de valores positivos
            if (peso <= 0 || katual <= 0 || kalvo <= 0 || velInfu <= 0 || volume <= 0) {
                if (peso <= 0) {
                    pesoEditText.error = "O peso deve ser maior que zero"
                }
                if (katual <= 0) {
                    kAtualEditText.error = "O potássio atual deve ser maior que zero"
                }
                if (kalvo <= 0) {
                    kDesejadoEditText.error = "O potássio alvo deve ser maior que zero"
                }
                if (velInfu <= 0) {
                    velInfuEditText.error = "A velocidade de infusão deve ser maior que zero"
                }
                if (volume <= 0) {
                    volumeEditText.error = "O volume de infusão deve ser maior que zero"
                }
                return
            }

            // Validação de ranges fisiológicos
            if (katual < 1.0 || katual > 8.0) {
                kAtualEditText.error = "Valor de potássio atual fora do range esperado (1.0-8.0 mEq/L)"
                return
            }

            if (kalvo < 1.0 || kalvo > 8.0) {
                kDesejadoEditText.error = "Valor de potássio alvo fora do range esperado (1.0-8.0 mEq/L)"
                return
            }

            if (kalvo <= katual) {
                kDesejadoEditText.error = "O potássio alvo deve ser maior que o atual"
                return
            }


            // Calcular déficit
            val deficit = (kalvo - katual) * peso * 0.4

            // Validar concentração máxima por via de acesso
            val isPeriferico = selectedIdAcesso == R.id.rbPeriferico
            val isCentral = selectedIdAcesso == R.id.rbCentral

            val concentracao_solucao_Litro = deficit / (volume/1000) // mEq/L

            if (isPeriferico && concentracao_solucao_Litro >= 40) {
                Toast.makeText(this, "Concentração máxima superior ao limite da via periférica. Considere acesso central.", Toast.LENGTH_LONG).show()
                return
            }

            if (isCentral && concentracao_solucao_Litro >= 80) {
                Toast.makeText(this, "Concentração superior ao máximo da via central. Considere fracionar a dose.", Toast.LENGTH_LONG).show()
                return
            }

            val formattedDeficit = String.format("%.2f", deficit)
            deficitValueTextView.text = "Déficit de K+: $formattedDeficit mEq"

            // Encontrar apresentação
            val apresentação = if (selectedIdApresentaçao == R.id.rb10) {
                10.0
            } else {
                19.1
            }

            // Calcular diluição
            val vol_aspirar = deficit / ((apresentação / 100) * 13.4)
            val vol_aspirar_arredondado = vol_aspirar.roundToInt()

            // Validar se o volume não excede o limite do frasco
            if (vol_aspirar_arredondado > 450) { // Deixando margem de segurança
                Toast.makeText(this, "Volume calculado muito alto. Considere fracionamento da dose.", Toast.LENGTH_LONG).show()
                return
            }

            val vol_soro = (volume - vol_aspirar_arredondado)

            if (vol_soro < 50) { // Mínimo de diluição
                Toast.makeText(this, "Volume de diluição muito baixo. Revise os parâmetros.", Toast.LENGTH_LONG).show()
                return
            }

            val formatted_vol_aspirar = vol_aspirar_arredondado.toString()
            val formatted_vol_soro = vol_soro.toString()

            volAspirarTextView.text = "Diluição: Aspirar $formatted_vol_aspirar mL e diluir em $formatted_vol_soro mL de SF 0,9%"

            // Calcular velocidade de infusão
            // velInfu está em mEq/hr, precisamos converter para mL/hr
            // Concentração total da solução = deficit mEq em 500 mL
            val concentracao_solucao = deficit / volume // mEq/mL
            val vel_infu_ml_hr = velInfu / concentracao_solucao // mL/hr

            val formatted_velocidade = String.format("%.1f", vel_infu_ml_hr)
            velocidadeTextView.text = "Taxa de infusão: $formatted_velocidade mL/h"

            // Calcular tempo total de infusão
            val tempo_total_horas = volume / vel_infu_ml_hr
            val formatted_tempo = String.format("%.1f", tempo_total_horas)
            tempoInfusaoTextView.text = "Tempo total de infusão: $formatted_tempo horas"

            resultCardView.visibility = View.VISIBLE

            // Scroll automático para mostrar o resultado
            resultCardView.post {
                scrollView.smoothScrollTo(0, resultCardView.bottom)
            }

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Erro: Verifique se todos os valores são números válidos", Toast.LENGTH_SHORT).show()
        }
    }
}