package com.example.mobile.features.pets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.google.android.material.button.MaterialButton

class PetListAdapter(
    private var pets: List<Pet>,
    private val onEdit: (Pet) -> Unit,
    private val onDelete: (Pet) -> Unit
) : RecyclerView.Adapter<PetListAdapter.PetListViewHolder>() {

    class PetListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPetName: TextView = view.findViewById(R.id.tvPetName)
        val tvPetMeta: TextView = view.findViewById(R.id.tvPetMeta)
        val tvPetDetails: TextView = view.findViewById(R.id.tvPetDetails)
        val btnEdit: MaterialButton = view.findViewById(R.id.btnEditPet)
        val btnDelete: MaterialButton = view.findViewById(R.id.btnDeletePet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pet_list_card, parent, false)
        return PetListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetListViewHolder, position: Int) {
        val pet = pets[position]
        holder.tvPetName.text = pet.name
        holder.tvPetMeta.text = listOfNotNull(pet.species, pet.breed).joinToString(" • ")

        val details = mutableListOf<String>()
        pet.age?.let { details.add("Age: $it") }
        pet.weight?.let { details.add("${it}kg") }
        pet.vaccinationStatus?.let { details.add("Vax: $it") }
        holder.tvPetDetails.text = if (details.isEmpty()) "No extra details" else details.joinToString(" • ")

        holder.btnEdit.setOnClickListener { onEdit(pet) }
        holder.btnDelete.setOnClickListener { onDelete(pet) }
    }

    override fun getItemCount() = pets.size

    fun updateData(newPets: List<Pet>) {
        pets = newPets
        notifyDataSetChanged()
    }
}
