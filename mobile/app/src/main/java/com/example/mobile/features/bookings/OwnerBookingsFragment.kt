package com.example.mobile.features.bookings

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OwnerBookingsFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvBookings: RecyclerView
    private lateinit var btnFilterPending: MaterialButton
    private lateinit var btnFilterActive: MaterialButton
    private lateinit var btnFilterCompleted: MaterialButton

    private lateinit var adapter: BookingListAdapter
    private var allBookings: List<Booking> = emptyList()

    private val selectedFilterTint by lazy {
        requireContext().getColor(R.color.blush_pink)
    }
    private val unselectedFilterTint by lazy {
        requireContext().getColor(R.color.soft_peach)
    }

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

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMyBookings("Bearer $token", upcoming = false)
                }
                if (!isAdded) return@launch

                if (response.isSuccessful) {
                    allBookings = response.body() ?: emptyList()
                    applyFilter("PENDING")
                } else {
                    showToast("Failed to load bookings")
                }
            } catch (e: Exception) {
                showToast("Error loading bookings")
            }
        }
    }

    private fun applyFilter(filter: String) {
        val filtered = when (filter) {
            "PENDING" -> allBookings.filter { it.status == "PENDING" }
            "COMPLETED" -> allBookings.filter { it.status == "COMPLETED" }
            else -> allBookings.filter { it.status == "CONFIRMED" }
        }
        updateFilterButtons(filter)
        adapter.updateData(filtered)
    }

    private fun updateFilterButtons(active: String) {
        if (!isAdded) return

        btnFilterPending.backgroundTintList = ColorStateList.valueOf(
            if (active == "PENDING") selectedFilterTint else unselectedFilterTint
        )
        btnFilterActive.backgroundTintList = ColorStateList.valueOf(
            if (active == "ACTIVE") selectedFilterTint else unselectedFilterTint
        )
        btnFilterCompleted.backgroundTintList = ColorStateList.valueOf(
            if (active == "COMPLETED") selectedFilterTint else unselectedFilterTint
        )
    }

    private fun cancelBooking(booking: Booking) {
        val token = prefsManager.getAuthToken() ?: return
        val request = UpdateOwnerBookingStatusRequest(status = "CANCELLED")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateOwnerBookingStatus("Bearer $token", booking.bookingId, request)
                }
                if (!isAdded) return@launch

                if (response.isSuccessful) {
                    loadBookings()
                } else {
                    showToast("Failed to cancel booking")
                }
            } catch (e: Exception) {
                showToast("Error cancelling booking")
            }
        }
    }

    private fun showToast(message: String) {
        context?.let { android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show() }
    }
}
