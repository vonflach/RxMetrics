package com.example.rxmetrics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rxmetrics.database.Medicamento
import com.example.rxmetrics.database.Doenca
import com.example.rxmetrics.database.DoencaComMedicamentos

class PrescricoesAdapter(
    private val onDeleteClick: (Medicamento) -> Unit
) : ListAdapter<DoencaComMedicamentos, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_DOENCA_HEADER = 0
        private const val TYPE_MEDICAMENTO = 1
    }

    private val expandedItems = mutableSetOf<Long>() // IDs dos medicamentos expandidos

    override fun getItemViewType(position: Int): Int {
        // Lógica para determinar se é header ou medicamento
        // Vamos criar uma lista flat com headers e medicamentos
        return if (isHeaderPosition(position)) TYPE_DOENCA_HEADER else TYPE_MEDICAMENTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DOENCA_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_doenca_header, parent, false)
                DoencaHeaderViewHolder(view)
            }
            TYPE_MEDICAMENTO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_medicamento, parent, false)
                MedicamentoViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getFlatItem(position)

        when (holder) {
            is DoencaHeaderViewHolder -> {
                if (item is FlatItem.DoencaHeader) {
                    holder.bind(item.doenca)
                }
            }
            is MedicamentoViewHolder -> {
                if (item is FlatItem.MedicamentoItem) {
                    val isExpanded = expandedItems.contains(item.medicamento.id)
                    holder.bind(item.medicamento, isExpanded) { medicamento ->
                        // Toggle expand/collapse
                        if (expandedItems.contains(medicamento.id)) {
                            expandedItems.remove(medicamento.id)
                        } else {
                            expandedItems.add(medicamento.id)
                        }
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return getFlatList().size
    }

    // Criar lista flat com headers e medicamentos
    private fun getFlatList(): List<FlatItem> {
        val flatList = mutableListOf<FlatItem>()

        currentList.forEach { doencaComMedicamentos ->
            // Adicionar header da doença
            flatList.add(FlatItem.DoencaHeader(doencaComMedicamentos.doenca))

            // Adicionar medicamentos
            doencaComMedicamentos.medicamentos.forEach { medicamento ->
                flatList.add(FlatItem.MedicamentoItem(medicamento))
            }
        }

        return flatList
    }

    private fun getFlatItem(position: Int): FlatItem {
        return getFlatList()[position]
    }

    private fun isHeaderPosition(position: Int): Boolean {
        return getFlatItem(position) is FlatItem.DoencaHeader
    }

    // ViewHolder para header da doença
    inner class DoencaHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDoenca: TextView = itemView.findViewById(R.id.tvDoenca)

        fun bind(doenca: Doenca) {
            tvDoenca.text = doenca.nome.uppercase()
        }
    }

    // ViewHolder para medicamento
    inner class MedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutHeader: LinearLayout = itemView.findViewById(R.id.layoutMedicamentoHeader)
        private val tvMedicamento: TextView = itemView.findViewById(R.id.tvMedicamento)
        private val tvDosePreview: TextView = itemView.findViewById(R.id.tvDosePreview)
        private val ivExpandir: ImageView = itemView.findViewById(R.id.ivExpandir)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val layoutDetalhes: LinearLayout = itemView.findViewById(R.id.layoutDetalhes)
        private val tvDose: TextView = itemView.findViewById(R.id.tvDose)
        private val tvPosologia: TextView = itemView.findViewById(R.id.tvPosologia)
        private val tvVia: TextView = itemView.findViewById(R.id.tvVia)
        private val tvObservacoes: TextView = itemView.findViewById(R.id.tvObservacoes)

        fun bind(
            medicamento: Medicamento,
            isExpanded: Boolean,
            onToggleClick: (Medicamento) -> Unit
        ) {
            // Dados básicos
            tvMedicamento.text = medicamento.nome
            tvDosePreview.text = "${medicamento.dose} - ${medicamento.via}"

            // Detalhes expandidos
            tvDose.text = medicamento.dose
            tvPosologia.text = medicamento.posologia
            tvVia.text = medicamento.via
            tvObservacoes.text = medicamento.observacoes

            // Estado de expansão
            layoutDetalhes.visibility = if (isExpanded) View.VISIBLE else View.GONE
            ivExpandir.rotation = if (isExpanded) 180f else 0f

            // Listeners
            layoutHeader.setOnClickListener {
                onToggleClick(medicamento)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(medicamento)
            }
        }
    }

    // Sealed class para items da lista flat
    sealed class FlatItem {
        data class DoencaHeader(val doenca: Doenca) : FlatItem()
        data class MedicamentoItem(val medicamento: Medicamento) : FlatItem()
    }

    // DiffUtil para performance
    class DiffCallback : DiffUtil.ItemCallback<DoencaComMedicamentos>() {
        override fun areItemsTheSame(
            oldItem: DoencaComMedicamentos,
            newItem: DoencaComMedicamentos
        ): Boolean {
            return oldItem.doenca.id == newItem.doenca.id
        }

        override fun areContentsTheSame(
            oldItem: DoencaComMedicamentos,
            newItem: DoencaComMedicamentos
        ): Boolean {
            return oldItem == newItem
        }
    }
}