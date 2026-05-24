package com.example.mobile.features.pets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PetListFragment : Fragment() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvPets: RecyclerView
    private lateinit var btnAddPet: ImageView
    private lateinit var adapter: PetListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pet_list, container, false)
        prefsManager = PreferencesManager(requireContext())

        rvPets = view.findViewById(R.id.rvPetList)
        btnAddPet = view.findViewById(R.id.btnAddPet)

        adapter = PetListAdapter(emptyList(),
            onEdit = { pet ->
                startActivity(AddEditPetActivity.newIntent(requireContext(), pet))
            },
            onDelete = { pet ->
                deletePet(pet)
            }
        )

        rvPets.layoutManager = LinearLayoutManager(requireContext())
        rvPets.adapter = adapter

        btnAddPet.setOnClickListener {
            startActivity(AddEditPetActivity.newIntent(requireContext(), null))
        }

        loadPets()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadPets()
    }

    private fun loadPets() {
        val token = prefsManager.getAuthToken() ?: return

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMyPets("Bearer $token")
                }
                if (response.isSuccessful) {
                    adapter.updateData(response.body() ?: emptyList())
                } else {
                    Toast.makeText(requireContext(), "Failed to load pets", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading pets", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deletePet(pet: Pet) {
        val token = prefsManager.getAuthToken() ?: return

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.deletePet("Bearer $token", pet.petId)
                }
                if (response.isSuccessful) {
                    loadPets()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete pet", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error deleting pet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
