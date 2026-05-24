package com.example.mobile.features.sitters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R

class SitterAdapter(
    private var sitters: List<SitterSummary>,
    private val onSelect: (SitterSummary) -> Unit
) : RecyclerView.Adapter<SitterAdapter.SitterViewHolder>() {

    class SitterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvSitterName)
        val tvMeta: TextView = view.findViewById(R.id.tvSitterMeta)
        val tvServices: TextView = view.findViewById(R.id.tvSitterServices)
        val tvRating: TextView = view.findViewById(R.id.tvSitterRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sitter_card, parent, false)
        return SitterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SitterViewHolder, position: Int) {
        val sitter = sitters[position]
        holder.tvName.text = sitter.fullName
        holder.tvMeta.text = listOfNotNull(sitter.location, sitter.experience).joinToString(" • ")
        holder.tvServices.text = if (sitter.servicesOffered.isEmpty()) {
            "No services listed"
        } else {
            sitter.servicesOffered.joinToString(", ")
        }
        val rating = sitter.rating?.toString() ?: "-"
        val reviews = sitter.reviewCount?.toString() ?: "0"
        holder.tvRating.text = "Rating: $rating ($reviews)"

        holder.itemView.setOnClickListener { onSelect(sitter) }
    }

    override fun getItemCount() = sitters.size

    fun updateData(newSitters: List<SitterSummary>) {
        sitters = newSitters
        notifyDataSetChanged()
    }
}
