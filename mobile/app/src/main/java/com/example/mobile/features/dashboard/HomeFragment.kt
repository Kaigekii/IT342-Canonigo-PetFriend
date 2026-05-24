package com.example.mobile.features.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.features.bookings.BookingAdapter
import com.example.mobile.features.pets.PetAdapter
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvPets: RecyclerView
    private lateinit var rvBookings: RecyclerView
    private lateinit var petAdapter: PetAdapter
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        prefsManager = PreferencesManager(requireContext())
        
        rvPets = view.findViewById(R.id.rvPets)
        rvBookings = view.findViewById(R.id.rvBookings)
        
        setupRecyclerViews()
        loadDashboardData()
        
        return view
    }
    
    private fun setupRecyclerViews() {
        petAdapter = PetAdapter(emptyList())
        rvPets.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvPets.adapter = petAdapter
        
        bookingAdapter = BookingAdapter(emptyList())
        rvBookings.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvBookings.adapter = bookingAdapter
    }
    
    private fun loadDashboardData() {
        val token = prefsManager.getAuthToken() ?: return
        
        scope.launch {
            try {
                // Fetch Pets
                val petsResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMyPets("Bearer $token")
                }
                if (petsResponse.isSuccessful) {
                    petsResponse.body()?.let { petAdapter.updateData(it) }
                }
                
                // Fetch Bookings
                val bookingsResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMyBookings("Bearer $token", upcoming = true)
                }
                if (bookingsResponse.isSuccessful) {
                    bookingsResponse.body()?.let { bookingAdapter.updateData(it) }
                }
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading dashboard", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
