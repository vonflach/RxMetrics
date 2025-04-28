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

class PesoIdealActivity : AppCompatActivity() {

    private lateinit var alturaEditText: TextInputEditText
    private lateinit var sexoRadioGroup: RadioGroup
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var pesoValueTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peso_ideal)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }

        // Inicializar as views
        alturaEditText = findViewById(R.id.etHeight)
        sexoRadioGroup = findViewById(R.id.rgSexo)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        pesoValueTextView = findViewById(R.id.tvPesoValue)

        // Configurar o botão de calcular
        calculateButton.setOnClickListener {
            calculatePesoIdeal()
        }
    }

    private fun calculatePesoIdeal() {
        val alturaStr = alturaEditText.text.toString()
        val selectedId = sexoRadioGroup.checkedRadioButtonId

        if (alturaStr.isEmpty()) {
            alturaEditText.error = "Por favor, insira a altura"
            return
        }

        if (selectedId == -1) {
            Toast.makeText(this, "Por favor, selecione o sexo", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val altura = alturaStr.toFloat()


            if (altura <= 0) {
                alturaEditText.error = "A altura deve ser maior que zero"
                return
            }

            val resultado: Double = when {
                altura < 152.4 -> {
                    if (selectedId == R.id.rbMasculino) {
                        50.0
                    } else {
                        45.5
                    }
                }
                selectedId == R.id.rbMasculino -> {
                    50 + (0.91 * (altura - 152.4))
                }
                else -> {
                    45.5 + (0.91 * (altura - 152.4))
                }
            }

            val formattedPeso = String.format("%.2f", resultado)

            pesoValueTextView.text = "Peso Ideal: $formattedPeso kg"

            resultCardView.visibility = View.VISIBLE

        } catch (e: NumberFormatException) {
            alturaEditText.error = "Altura inválida"
            return
        }
    }
}