package com.example.mobile.features.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class SitterRequestsFragment : Fragment() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvRequests: RecyclerView
    private lateinit var adapter: SitterRequestAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sitter_requests, container, false)
        prefsManager = PreferencesManager(requireContext())

        rvRequests = view.findViewById(R.id.rvSitterRequests)
        adapter = SitterRequestAdapter(emptyList(),
            onAccept = { booking -> updateStatus(booking, "CONFIRMED") },
            onDecline = { booking -> updateStatus(booking, "CANCELLED") }
        )

        rvRequests.layoutManager = LinearLayoutManager(requireContext())
        rvRequests.adapter = adapter

        loadRequests()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadRequests()
    }

    private fun loadRequests() {
        val token = prefsManager.getAuthToken() ?: return

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getSitterPendingBookings("Bearer $token")
                }
                if (response.isSuccessful) {
                    adapter.updateData(response.body() ?: emptyList())
                } else {
                    Toast.makeText(requireContext(), "Failed to load requests", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading requests", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(booking: Booking, status: String) {
        val token = prefsManager.getAuthToken() ?: return
        val request = UpdateSitterBookingStatusRequest(status = status)

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateSitterBookingStatus("Bearer $token", booking.bookingId, request)
                }
                if (response.isSuccessful) {
                    loadRequests()
                } else {
                    Toast.makeText(requireContext(), "Failed to update request", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error updating request", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
