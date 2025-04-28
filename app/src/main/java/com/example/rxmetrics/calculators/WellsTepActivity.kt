package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R

class WellsTepActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var resultTextView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var cbTvpTep: CheckBox
    private lateinit var cbTaquicardia: CheckBox
    private lateinit var cbImobilizacao: CheckBox
    private lateinit var cbHemoptise: CheckBox
    private lateinit var cbCancer: CheckBox
    private lateinit var cbSinaisTvp: CheckBox
    private lateinit var cbDiagnosticoAlternativo: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wells_tep)

        // Inicialização dos componentes
        backButton = findViewById(R.id.btnBack)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        resultTextView = findViewById(R.id.tvResultadoTep)
        scrollView = findViewById(R.id.scrollView)

        cbTvpTep = findViewById(R.id.cbTvpTep)
        cbTaquicardia = findViewById(R.id.cbTaquicardia)
        cbImobilizacao = findViewById(R.id.cbImobilizacao)
        cbHemoptise = findViewById(R.id.cbHemoptise)
        cbCancer = findViewById(R.id.cbCancer)
        cbSinaisTvp = findViewById(R.id.cbSinaisTvp)
        cbDiagnosticoAlternativo = findViewById(R.id.cbDiagnosticoAlternativo)

        backButton.setOnClickListener {
            onBackPressed()
        }

        calculateButton.setOnClickListener {
            calculateWellsScore()
            // Scroll automático para o resultado com um pequeno delay para garantir que o layout foi atualizado
            scrollView.postDelayed({
                scrollView.smoothScrollTo(0, resultCardView.bottom)
            }, 200)
        }
    }

    private fun calculateWellsScore() {
        var score = 0.0

        // Critérios de Wells para TEP com suas respectivas pontuações
        if (cbTvpTep.isChecked) score += 1.5
        if (cbTaquicardia.isChecked) score += 1.5
        if (cbImobilizacao.isChecked) score += 1.5
        if (cbHemoptise.isChecked) score += 1
        if (cbCancer.isChecked) score += 1
        if (cbSinaisTvp.isChecked) score += 3
        if (cbDiagnosticoAlternativo.isChecked) score += 3

        val risk = when {
            score > 6 -> "Alto risco"
            score in 2.0..6.0 -> "Risco moderado"
            else -> "Baixo risco"
        }

        resultTextView.text = "Pontuação: $score\n$risk"
        resultCardView.visibility = View.VISIBLE

        // Scroll automático até o Card de resultado
        scrollView.postDelayed({
            scrollView.smoothScrollTo(0, resultCardView.bottom)
        }, 200)
    }
}