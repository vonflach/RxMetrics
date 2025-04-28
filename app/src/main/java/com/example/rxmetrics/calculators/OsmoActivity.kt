package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R
import com.example.rxmetrics.databinding.ActivityOsmoSericaBinding
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.pow

class OsmoActivity : AppCompatActivity() {

    private lateinit var sodioEditText: TextInputEditText
    private lateinit var glicoseEditText: TextInputEditText
    private lateinit var escoriaRadioGroup: RadioGroup
    private lateinit var nitroEditText: TextInputEditText
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var osmoValueTextView: TextView

    private lateinit var binding: ActivityOsmoSericaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOsmoSericaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()  // Isso vai chamar a ação de voltar à tela anterior
        }

        sodioEditText = findViewById(R.id.etSodio)
        glicoseEditText = findViewById(R.id.etGlicose)
        escoriaRadioGroup = findViewById(R.id.rgEscoria)
        nitroEditText = findViewById(R.id.etNitro)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        osmoValueTextView = findViewById(R.id.tvOsmoValue)

        calculateButton.setOnClickListener {
            calculateOsmo()
        }
    }

    private fun calculateOsmo() {
        val selectedEscoria = escoriaRadioGroup.checkedRadioButtonId
        val nitrostr = nitroEditText.text.toString()
        val sodiostr = sodioEditText.text.toString()
        val glicosestr = glicoseEditText.text.toString()

        if (nitrostr.isEmpty() || sodiostr.isEmpty() || glicosestr.isEmpty()) {
            // Mostrar mensagem de erro
            if (nitrostr.isEmpty()) {
                nitroEditText.error = "Por favor, insira a escória"
            }
            if (sodiostr.isEmpty()) {
                sodioEditText.error = "Por favor, insira o sódio"
            }
            if (glicosestr.isEmpty()) {
                glicoseEditText.error = "Por favor, insira a glicose"
            }
            return
        }

        try {
            val nitro = nitrostr.toFloat()
            val sodio = sodiostr.toFloat()
            val glicose = glicosestr.toFloat()

            if (nitro <= 0 || sodio <= 0 || glicose <= 0) {
                if (nitro <= 0) {
                    nitroEditText.error = "A escória deve ser maior que zero"
                }
                if (sodio <= 0) {
                    sodioEditText.error = "O sódio deve ser maior que zero"
                }
                if (glicose <= 0) {
                    glicoseEditText.error = "A glicose deve ser maior que zero"
                }
                return
            }

            val osmo = if (selectedEscoria == R.id.rbUreia) {
                (2 * sodio) + (glicose / 18) + (nitro / 6)
            } else {
                (2 * sodio) + (glicose / 18) + (nitro / 2.8)
            }

            val formattedOsmo = String.format("%.1f", osmo)

            osmoValueTextView.text = "Osmolaridade Plasmática: $formattedOsmo Osm"

            resultCardView.visibility = View.VISIBLE
        } catch (e: NumberFormatException) {
            // Tratar erro de conversão numérica
            sodioEditText.error = "Formato de número inválido"
            nitroEditText.error = "Formato de número inválido"
            glicoseEditText.error = "Formato de número inválido"
        }
    }
}