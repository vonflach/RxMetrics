package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R

class WellsTvpActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var resultTextView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var cbCancer: CheckBox
    private lateinit var cbParalisia: CheckBox
    private lateinit var cbCirurgia: CheckBox
    private lateinit var cbDor: CheckBox
    private lateinit var cbEdemaTotal: CheckBox
    private lateinit var cbCircunferencia: CheckBox
    private lateinit var cbEdemaAssimetrico: CheckBox
    private lateinit var cbVeias: CheckBox
    private lateinit var cbTvpPrevia: CheckBox
    private lateinit var cbDiagnosticoAlternativo: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wells_tvp)

        // Inicialização dos componentes
        backButton = findViewById(R.id.btnBack)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        resultTextView = findViewById(R.id.tvResultadoTvp)
        scrollView = findViewById(R.id.scrollView)

        cbCancer = findViewById(R.id.cbCancer)
        cbParalisia = findViewById(R.id.cbParalisia)
        cbCirurgia = findViewById(R.id.cbCirurgia)
        cbDor = findViewById(R.id.cbDor)
        cbEdemaTotal = findViewById(R.id.cbEdemaTotal)
        cbCircunferencia = findViewById(R.id.cbCircunferencia)
        cbEdemaAssimetrico = findViewById(R.id.cbEdemaAssimetrico)
        cbVeias = findViewById(R.id.cbVeias)
        cbTvpPrevia = findViewById(R.id.cbTvpPrevia)
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
        var score = 0
        if (cbCancer.isChecked) score++
        if (cbParalisia.isChecked) score++
        if (cbCirurgia.isChecked) score++
        if (cbDor.isChecked) score++
        if (cbEdemaTotal.isChecked) score++
        if (cbCircunferencia.isChecked) score++
        if (cbEdemaAssimetrico.isChecked) score++
        if (cbVeias.isChecked) score++
        if (cbTvpPrevia.isChecked) score++
        if (cbDiagnosticoAlternativo.isChecked) score -= 2

        val risk = when {
            score >= 3 -> "Alto risco"
            score in 1..2 -> "Risco moderado"
            else -> "Baixo risco"
        }

        resultTextView.text = "Pontuação: $score\n$risk"
        resultCardView.visibility = View.VISIBLE
    }
}