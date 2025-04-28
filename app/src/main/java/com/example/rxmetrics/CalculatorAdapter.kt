package com.example.rxmetrics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CalculatorAdapter(
    private val calculators: List<Calculator>,
    private val onItemClick: (Calculator) -> Unit
) : RecyclerView.Adapter<CalculatorAdapter.CalculatorViewHolder>() {

    class CalculatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val textName: TextView = itemView.findViewById(R.id.tvCalcName)
        val textDescription: TextView = itemView.findViewById(R.id.tvCalcDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculatorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calculator, parent, false)
        return CalculatorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalculatorViewHolder, position: Int) {
        val calculator = calculators[position]

        holder.textName.text = calculator.name
        holder.textDescription.text = calculator.description

        holder.cardView.setOnClickListener {
            onItemClick(calculator)
        }
    }

    override fun getItemCount() = calculators.size
}