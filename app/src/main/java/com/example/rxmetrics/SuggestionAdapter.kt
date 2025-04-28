package com.example.rxmetrics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SuggestionAdapter(
    private val suggestions: MutableList<Calculator>,
    private val onSuggestionClick: (Calculator) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textSuggestion: TextView = itemView.findViewById(R.id.tvSuggestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.textSuggestion.text = suggestion.name

        holder.itemView.setOnClickListener {
            onSuggestionClick(suggestion)
        }
    }

    override fun getItemCount() = suggestions.size

    fun updateSuggestions(newSuggestions: List<Calculator>) {
        suggestions.clear()
        suggestions.addAll(newSuggestions)
        notifyDataSetChanged()
    }
}