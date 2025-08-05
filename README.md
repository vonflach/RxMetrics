# Rx Metrics

## Sobre o Aplicativo

Rx Metrics é um aplicativo Android de calculadoras médicas desenvolvido para auxiliar profissionais de saúde em cálculos clínicos frequentes.

## Funcionalidades

O aplicativo inclui as seguintes calculadoras médicas  (ordem de inclusão):

- **Calculadora de IMC**: Calcula o Índice de Massa Corporal
- **Calculadora de Dose**: Calcula dosagens de medicamentos em vazão contínua
- **Calculadora de Clearance de Creatinina**: Estima a função renal do paciente pelos métodos de Cockcroft-Gault, MDRD e CKD-EPI (2021)
- **Calculadora de Peso Ideal**: Determina o peso ideal baseado na altura e sexo
- **Calculadora de Osmolaridade**: Calcula a osmolaridade sérica
- **Calculadora de Sódio corrigido pela Glicemia**: Corrige valores de sódio de acordo com a glicemia
- **Calculadora de PAM**: Calcula a Pressão Arterial Média
- **Calculadora de Cálcio corrigido pela Albumina**: Ajusta o cálcio sérico baseado em níveis de albumina
- **Calculadora de Wells para TVP**: Avalia o risco de Trombose Venosa Profunda pelos critérios de Wells
- **Calculadora de Fluido Intraoperatório**: Calcula a quantidade de fluidos durante o ato cirúrgico
- **Calculadora de LDL**: Calcula o LDL colesterol
- **Calculadora de Wells para TEP**: Avalia o risco de Tromboembolismo Pulmonar pelos critérios de Wells
- **Calculadora de Déficit de Potássio**: Avalia a quantidade faltante do íon e sugere a reposição necessária (Somente IV)
- **Calculadora de Correção de Sódio para Hiper e hiponatremia**: Calcula a velocidade de infusão e sugere a diluição a partir da variação sérica de sódio
- **Página para Prescrições Rápidas**: Uma página com prescrições que o usuário pode personalizar a partir de sua prática clínica

##  Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **IDE**: Android Studio
- **UI**:
    - Jetpack Compose (interface moderna declarativa)
    - View Binding (para componentes de UI tradicionais)
- **Bibliotecas**:
    - Material Design Components
    - AndroidX Components:
        - AppCompat
        - CardView
        - RecyclerView
        - Core KTX
        - Lifecycle Runtime KTX
    - Jetpack Compose:
        - Material 3
        - UI Graphics
        - UI Tooling
    - JUnit e Espresso (para testes)

## Instalação

1. Baixe o APK da [página de releases](https://github.com/vonflach/RxMetrics/releases)
2. Habilite a instalação de fontes desconhecidas em seu dispositivo Android
3. Instale o aplicativo clicando no arquivo APK baixado
4. Abra o aplicativo e comece a utilizar

Alternativamente, você pode compilar o projeto a partir do código-fonte:

```
git clone https://github.com/vonflach/RxMetrics.git
cd RxMetrics
```

Abra o projeto no Android Studio e execute-o em um emulador ou dispositivo físico.

## Requisitos do Sistema

- Android 7.0 (Nougat) ou superior
- Aproximadamente 20MB de espaço de armazenamento
- Não requer permissões especiais

## 🔜 Próximas Atualizações

- Modo escuro
- Novas calculadoras específicas para diferentes especialidades médicas

## ⚠️ Aviso Legal

Este aplicativo foi desenvolvido como uma ferramenta de apoio pessoal à prática clínica do desenvolvedor.

> ⚠️ **Este aplicativo não substitui a avaliação médica, o julgamento profissional ou diretrizes clínicas formais. Não siga cegamente as informações aqui disponibilizadas.**

Apesar de toda dedicação e esforço para garantir a precisão dos cálculos e dados, o desenvolvedor **não assume responsabilidade por quaisquer decisões clínicas** tomadas com base nos resultados fornecidos pelo aplicativo.

**Use por sua conta e risco.** Sempre verifique valores críticos e valide as informações com fontes confiáveis e protocolos da sua instituição.

**Bom exercício clínico!**


## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

## 📞 Contato

**GitHub**: [@vonflach](https://github.com/vonflach)

---

Desenvolvido por [@vonflach](https://github.com/vonflach) © 2025