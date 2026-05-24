package com.example.mobile.features.pets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R

class PetAdapter(private var pets: List<Pet>) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPetPhoto: ImageView = view.findViewById(R.id.ivPetPhoto)
        val tvPetName: TextView = view.findViewById(R.id.tvPetName)
        val tvPetSpecies: TextView = view.findViewById(R.id.tvPetSpecies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pet_card, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = pets[position]
        holder.tvPetName.text = pet.name
        holder.tvPetSpecies.text = pet.species
    }

    override fun getItemCount() = pets.size
    
    fun updateData(newPets: List<Pet>) {
        pets = newPets
        notifyDataSetChanged()
    }
}
