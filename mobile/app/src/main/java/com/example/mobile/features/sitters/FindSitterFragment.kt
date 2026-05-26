package com.example.mobile.features.sitters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

class FindSitterFragment : Fragment() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager
    private lateinit var etLocation: EditText
    private lateinit var spService: Spinner
    private lateinit var btnSearch: Button
    private lateinit var rvSitters: RecyclerView
    private lateinit var adapter: SitterAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_find_sitter, container, false)
        prefsManager = PreferencesManager(requireContext())

        etLocation = view.findViewById(R.id.etSitterLocation)
        spService = view.findViewById(R.id.spSitterService)
        btnSearch = view.findViewById(R.id.btnSitterSearch)
        rvSitters = view.findViewById(R.id.rvSitters)

        val serviceOptions = listOf("Any service", "WALK", "FEEDING", "OVERNIGHT", "PLAYTIME")
        val serviceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceOptions)
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spService.adapter = serviceAdapter

        adapter = SitterAdapter(emptyList()) { sitter ->
            val intent = Intent(requireContext(), SitterProfileActivity::class.java)
            intent.putExtra(SitterProfileActivity.EXTRA_SITTER_ID, sitter.sitterId)
            startActivity(intent)
        }

        rvSitters.layoutManager = LinearLayoutManager(requireContext())
        rvSitters.adapter = adapter

        btnSearch.setOnClickListener { searchSitters() }

        searchSitters()

        return view
    }

    private fun searchSitters() {
        val token = prefsManager.getAuthToken() ?: return
        val location = etLocation.text.toString().trim().ifEmpty { null }
        val selectedService = spService.selectedItem.toString().trim()
        val serviceType = if (selectedService == "Any service") null else selectedService

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.searchSitters("Bearer $token", location, serviceType)
                }
                if (response.isSuccessful) {
                    if (!isAdded) return@launch
                    adapter.updateData(response.body() ?: emptyList())
                } else {
                    if (!isAdded) return@launch
                    context?.let {
                        Toast.makeText(it, "Failed to load sitters", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                if (!isAdded) return@launch
                context?.let {
                    Toast.makeText(it, "Error loading sitters", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
