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
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OwnerBookingsFragment : Fragment() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvBookings: RecyclerView
    private lateinit var btnFilterPending: MaterialButton
    private lateinit var btnFilterActive: MaterialButton
    private lateinit var btnFilterCompleted: MaterialButton

    private lateinit var adapter: BookingListAdapter
    private var allBookings: List<Booking> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_owner_bookings, container, false)
        prefsManager = PreferencesManager(requireContext())

        rvBookings = view.findViewById(R.id.rvOwnerBookings)
        btnFilterPending = view.findViewById(R.id.btnFilterPending)
        btnFilterActive = view.findViewById(R.id.btnFilterActive)
        btnFilterCompleted = view.findViewById(R.id.btnFilterCompleted)

        adapter = BookingListAdapter(emptyList(), onCancel = { booking ->
            cancelBooking(booking)
        })

        rvBookings.layoutManager = LinearLayoutManager(requireContext())
        rvBookings.adapter = adapter

        btnFilterPending.setOnClickListener { applyFilter("PENDING") }
        btnFilterActive.setOnClickListener { applyFilter("ACTIVE") }
        btnFilterCompleted.setOnClickListener { applyFilter("COMPLETED") }

        loadBookings()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadBookings()
    }

    private fun loadBookings() {
        val token = prefsManager.getAuthToken() ?: return

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMyBookings("Bearer $token", upcoming = false)
                }
                if (response.isSuccessful) {
                    allBookings = response.body() ?: emptyList()
                    applyFilter("PENDING")
                } else {
                    Toast.makeText(requireContext(), "Failed to load bookings", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading bookings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyFilter(filter: String) {
        val filtered = when (filter) {
            "PENDING" -> allBookings.filter { it.status == "PENDING" }
            "COMPLETED" -> allBookings.filter { it.status == "COMPLETED" }
            else -> allBookings.filter { it.status == "CONFIRMED" }
        }
        adapter.updateData(filtered)
    }

    private fun cancelBooking(booking: Booking) {
        val token = prefsManager.getAuthToken() ?: return
        val request = UpdateOwnerBookingStatusRequest(status = "CANCELLED")

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateOwnerBookingStatus("Bearer $token", booking.bookingId, request)
                }
                if (response.isSuccessful) {
                    loadBookings()
                } else {
                    Toast.makeText(requireContext(), "Failed to cancel booking", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cancelling booking", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
