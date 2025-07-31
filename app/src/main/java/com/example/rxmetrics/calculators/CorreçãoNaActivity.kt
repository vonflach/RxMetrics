package com.example.rxmetrics.calculators

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.rxmetrics.R
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.pow
import kotlin.math.roundToInt

class CorreçãoNaActivity: AppCompatActivity() {

    private lateinit var naAtualEditText: TextInputEditText
    private lateinit var naAlvoEditText: TextInputEditText
    private lateinit var pesoEditText: TextInputEditText
    private lateinit var tempoInfusaoEditText: TextInputEditText
    private lateinit var faixaEtariaRadioGroup: RadioGroup
    private lateinit var sexoRadioGroup: RadioGroup
    private lateinit var hiperRadioGroup: RadioGroup
    private lateinit var hipoRadioGroup: RadioGroup
    private lateinit var calculateButton: Button
    private lateinit var resultCardView: CardView
    private lateinit var velocidadeInfusaoTextView: TextView
    private lateinit var diluicaoSugeridaTextView: TextView
    //private lateinit var volAspirarTextView: TextView
    //private lateinit var velocidadeTextView: TextView
    //private lateinit var tempoInfusaoTextView: TextView
    private lateinit var tvSolHiper: TextView
    private lateinit var tvSolHipo: TextView
    private lateinit var scrollView: androidx.core.widget.NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correcao_na)

        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        naAtualEditText = findViewById(R.id.etNaAtual)
        naAlvoEditText = findViewById(R.id.etNaAlvo)
        pesoEditText = findViewById(R.id.etNaPeso)
        tempoInfusaoEditText = findViewById(R.id.etTempoNa)
        faixaEtariaRadioGroup = findViewById(R.id.rgFaixaEt)
        sexoRadioGroup = findViewById(R.id.rgSexoNa)
        hiperRadioGroup = findViewById(R.id.rgSolHiper)
        hipoRadioGroup = findViewById(R.id.rgSolHipo)
        calculateButton = findViewById(R.id.btnCalculate)
        resultCardView = findViewById(R.id.cvResult)
        velocidadeInfusaoTextView = findViewById(R.id.tvVelInfu)
        diluicaoSugeridaTextView = findViewById(R.id.tvDiluicao)
        scrollView = findViewById(R.id.scrollView)
        tvSolHiper = findViewById(R.id.tvSolHiper)
        tvSolHipo = findViewById(R.id.tvSolHipo)

        tvSolHiper.visibility = View.GONE
        hiperRadioGroup.visibility = View.GONE
        tvSolHipo.visibility = View.GONE
        hipoRadioGroup.visibility = View.GONE

        // Adicionar listener para o campo de sódio atual
        naAtualEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                checkSodiumLevelAndShowSolutions()
            }
        }

        naAtualEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                checkSodiumLevelAndShowSolutions()
            }
        })

        calculateButton.setOnClickListener {
            calculateInfusao()
        }
    }

    // Função para verificar o nível de sódio e mostrar/esconder os grupos apropriados
    private fun checkSodiumLevelAndShowSolutions() {
        val naAtualStr = naAtualEditText.text.toString()

        if (naAtualStr.isNotEmpty()) {
            try {
                val naAtual = naAtualStr.toFloat()

                when {
                    naAtual < 135 -> {
                        // Hiponatremia - mostrar soluções hipertônicas
                        tvSolHiper.visibility = View.VISIBLE
                        hiperRadioGroup.visibility = View.VISIBLE
                        tvSolHipo.visibility = View.GONE
                        hipoRadioGroup.visibility = View.GONE

                        // Limpar seleção do grupo escondido
                        hipoRadioGroup.clearCheck()
                    }
                    naAtual > 145 -> {
                        // Hipernatremia - mostrar soluções hipotônicas
                        tvSolHipo.visibility = View.VISIBLE
                        hipoRadioGroup.visibility = View.VISIBLE
                        tvSolHiper.visibility = View.GONE
                        hiperRadioGroup.visibility = View.GONE

                        // Limpar seleção do grupo escondido
                        hiperRadioGroup.clearCheck()
                    }
                    else -> {
                        // Valores normais (135-145) - esconder ambos ou mostrar ambos conforme sua preferência
                        tvSolHiper.visibility = View.GONE
                        hiperRadioGroup.visibility = View.GONE
                        tvSolHipo.visibility = View.GONE
                        hipoRadioGroup.visibility = View.GONE

                        // Limpar ambas as seleções
                        hiperRadioGroup.clearCheck()
                        hipoRadioGroup.clearCheck()
                    }
                }
            } catch (e: NumberFormatException) {
                // Se não conseguir converter, esconder ambos os grupos
                tvSolHiper.visibility = View.GONE
                hiperRadioGroup.visibility = View.GONE
                tvSolHipo.visibility = View.GONE
                hipoRadioGroup.visibility = View.GONE
            }
        } else {
            // Campo vazio - esconder ambos os grupos
            tvSolHiper.visibility = View.GONE
            hiperRadioGroup.visibility = View.GONE
            tvSolHipo.visibility = View.GONE
            hipoRadioGroup.visibility = View.GONE
        }
    }

    private fun calculateInfusao(){

        val naAtualStr = naAtualEditText.text.toString()
        val naAlvoStr = naAlvoEditText.text.toString()
        val pesoStr = pesoEditText.text.toString()
        val tempoInfusaoStr = tempoInfusaoEditText.text.toString()
        val selectedIdSexo = sexoRadioGroup.checkedRadioButtonId
        val selectedIdFaixaEtaria = faixaEtariaRadioGroup.checkedRadioButtonId
        val selectedIdHiper = hiperRadioGroup.checkedRadioButtonId
        val selectedIdHipo = hipoRadioGroup.checkedRadioButtonId

        //validação campo vazio
        if (naAtualStr.isEmpty() || naAlvoStr.isEmpty() || pesoStr.isEmpty() || tempoInfusaoStr.isEmpty()) {
            if (naAtualStr.isEmpty()) {
                naAtualEditText.error = "Por favor, insira o sódio atual"
            }
            if (naAlvoStr.isEmpty()){
                naAlvoEditText.error = "Por favor, insira o sódio alvo"
            }
            if (pesoStr.isEmpty()){
                pesoEditText.error = "Por favor, insira o peso"
            }
            if (tempoInfusaoStr.isEmpty()){
                tempoInfusaoEditText.error = "Por favor, insira o tempo de infusão"
            }
            return
        }

        if (selectedIdSexo == -1) {
            Toast.makeText(this, "Por favor, selecione o sexo", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedIdFaixaEtaria == -1) {
            Toast.makeText(this, "Por favor, selecione a faixa etária", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar apenas o grupo que estiver visível
        if (tvSolHiper.visibility == View.VISIBLE && selectedIdHiper == -1) {
            Toast.makeText(this, "Por favor, selecione a solução hipertônica", Toast.LENGTH_SHORT).show()
            return
        }

        if (tvSolHipo.visibility == View.VISIBLE && selectedIdHipo == -1) {
            Toast.makeText(this, "Por favor, selecione a solução hipotônica", Toast.LENGTH_SHORT).show()
            return
        }

        try {

            val naAtual = naAtualStr.toFloat()
            val naAlvo = naAlvoStr.toFloat()
            val pesoNa = pesoStr.toFloat()
            val tempoInfusao = tempoInfusaoStr.toFloat()

            //validação valores
            if (naAtual <= 0 || naAlvo <= 0 || pesoNa <= 0 || tempoInfusao <= 0){
                if (naAtual <= 0){
                    naAtualEditText.error = "O sódio atual deve ser maior que zero"
                }
                if (naAlvo <= 0){
                    naAlvoEditText.error = "O sódio alvo deve ser maior que zero"
                }
                if (pesoNa <= 0){
                    pesoEditText.error = "O peso deve ser maior que zero"
                }
                if(tempoInfusao <= 0){
                    tempoInfusaoEditText.error = "O tempo de infusão deve ser maior que zero"
                }
                return
            }

            //constante sexo
            val constante = when {
                selectedIdSexo == R.id.rbMasc && selectedIdFaixaEtaria == R.id.rbAdulto -> 0.6
                selectedIdSexo == R.id.rbMasc && selectedIdFaixaEtaria == R.id.rbIdoso -> 0.5
                selectedIdSexo == R.id.rbFem && selectedIdFaixaEtaria == R.id.rbAdulto -> 0.5
                selectedIdSexo == R.id.rbFem && selectedIdFaixaEtaria == R.id.rbIdoso -> 0.45
                else -> 0.5 // valor padrão
            }

            val naSol = when {
                selectedIdHiper == R.id.rb09 -> 153
                selectedIdHiper == R.id.rb3 -> 513
                selectedIdHipo == R.id.rb045 -> 77
                selectedIdHipo == R.id.rbSG5 -> 0
                else -> 0
            }

            //calcular vazão
            val vazao = (1000*(naAlvo-naAtual)*((constante*pesoNa)+1))/((naSol-naAtual)*tempoInfusao)

            val formattedVazao = String.format("%.0f", vazao)
            velocidadeInfusaoTextView.text = "Infundir a $formattedVazao mL/hr"

            diluicaoSugeridaTextView.text = when {
                selectedIdHiper == R.id.rb09 -> "500 mL de SF 0,9%"
                selectedIdHiper == R.id.rb3 -> "50 mL de NaCl 20% + 450 mL de SF 0,9%"
                selectedIdHipo == R.id.rb045 -> "10 mL de NaCl 20% + 490 mL de Água Destilada"
                selectedIdHipo == R.id.rbSG5 -> "500 mL de SG 5%"
                else -> "Selecione uma solução"
            }

            resultCardView.visibility = View.VISIBLE

            // Scroll automático para mostrar o resultado
            resultCardView.post {
                scrollView.smoothScrollTo(0, resultCardView.bottom)
            }
        } catch (e: NumberFormatException) {

            naAtualEditText.error = "Formato de número inválido"
            naAlvoEditText.error = "Formato de número inválido"
            pesoEditText.error = "Formato de número inválido"
            tempoInfusaoEditText.error = "Formato de número inválido"

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}