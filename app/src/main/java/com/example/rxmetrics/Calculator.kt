package com.example.rxmetrics

data class Calculator(
    val id: Int,
    val name: String,
    val description: String,
    val type: CalculatorType
)

enum class CalculatorType {
    BMI, // Índice de Massa Corporal
    DOSE_CALC, // Calculadora de dose
    CREATININE_CLEARANCE, // Clearance de Creatinina
    PESO_IDEAL,
    PEDIATRIC_DOSE, // Dose Pediátrica
    PREGNANCY_CALCULATOR, // Calculadora de Gestação
    GLASGOW_SCALE, // Escala de Glasgow
    BODY_SURFACE_AREA,
    OSMO_SERICA,
    PAM,
    SODIO_GLICEMIA,
    CALCIO_ALBUMINA,
    FIB4,
    WELLS_TVP,
    WELLS_TEP,
    PESI,
    GENEVA_TEP,
    CHILD_PUGH,
    MELD,
    LDL,
    FLUIDO_INTRA_OP,
    FENA,
    DEFICIT_K
// Área de Superfície Corporal
    // Adicione mais tipos conforme necessário
}