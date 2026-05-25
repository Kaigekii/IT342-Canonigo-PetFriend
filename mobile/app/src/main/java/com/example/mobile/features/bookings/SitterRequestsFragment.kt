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

class SitterRequestsFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvRequests: RecyclerView
    private lateinit var adapter: SitterRequestAdapter

    private lateinit var btnTabAll: MaterialButton
    private lateinit var btnTabPending: MaterialButton
    private lateinit var btnTabConfirmed: MaterialButton
    private lateinit var btnTabCompleted: MaterialButton

    private var allRequests: List<Booking> = emptyList()
    private var activeTab: String = "ALL"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sitter_requests, container, false)
        prefsManager = PreferencesManager(requireContext())

        rvRequests = view.findViewById(R.id.rvSitterRequests)
        adapter = SitterRequestAdapter(
            emptyList(),
            onAccept = { booking -> updateStatus(booking, "CONFIRMED") },
            onDecline = { booking -> updateStatus(booking, "CANCELLED") },
            onComplete = { booking -> updateStatus(booking, "COMPLETED") }
        )

        btnTabAll = view.findViewById(R.id.btnTabAll)
        btnTabPending = view.findViewById(R.id.btnTabPending)
        btnTabConfirmed = view.findViewById(R.id.btnTabConfirmed)
        btnTabCompleted = view.findViewById(R.id.btnTabCompleted)

        btnTabAll.setOnClickListener { setActiveTab("ALL") }
        btnTabPending.setOnClickListener { setActiveTab("PENDING") }
        btnTabConfirmed.setOnClickListener { setActiveTab("CONFIRMED") }
        btnTabCompleted.setOnClickListener { setActiveTab("COMPLETED") }

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

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getSitterBookings("Bearer $token")
                }

                if (!isAdded) return@launch

                if (response.isSuccessful) {
                    allRequests = response.body() ?: emptyList()
                    applyFilter()
                } else {
                    showToast("Failed to load requests")
                }
            } catch (e: Exception) {
                showToast("Error loading requests")
            }
        }
    }

    private fun setActiveTab(tab: String) {
        activeTab = tab
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = when (activeTab) {
            "PENDING" -> allRequests.filter { it.status == "PENDING" }
            "CONFIRMED" -> allRequests.filter { it.status == "CONFIRMED" }
            "COMPLETED" -> allRequests.filter { it.status == "COMPLETED" }
            else -> allRequests
        }
        updateTabLabels()
        adapter.updateData(filtered)
    }

    private fun updateTabLabels() {
        if (!isAdded) return

        val allCount = allRequests.size
        val pendingCount = allRequests.count { it.status == "PENDING" }
        val confirmedCount = allRequests.count { it.status == "CONFIRMED" }
        val completedCount = allRequests.count { it.status == "COMPLETED" }

        btnTabAll.text = "All ($allCount)"
        btnTabPending.text = "Pending ($pendingCount)"
        btnTabConfirmed.text = "Confirmed ($confirmedCount)"
        btnTabCompleted.text = "Completed ($completedCount)"

        val activeTint = requireContext().getColor(R.color.gray_dark)
        val inactiveTint = requireContext().getColor(R.color.soft_peach)
        val activeText = requireContext().getColor(R.color.web_surface)
        val inactiveText = requireContext().getColor(R.color.gray_dark)

        btnTabAll.backgroundTintList = ColorStateList.valueOf(if (activeTab == "ALL") activeTint else inactiveTint)
        btnTabPending.backgroundTintList = ColorStateList.valueOf(if (activeTab == "PENDING") activeTint else inactiveTint)
        btnTabConfirmed.backgroundTintList = ColorStateList.valueOf(if (activeTab == "CONFIRMED") activeTint else inactiveTint)
        btnTabCompleted.backgroundTintList = ColorStateList.valueOf(if (activeTab == "COMPLETED") activeTint else inactiveTint)

        btnTabAll.setTextColor(if (activeTab == "ALL") activeText else inactiveText)
        btnTabPending.setTextColor(if (activeTab == "PENDING") activeText else inactiveText)
        btnTabConfirmed.setTextColor(if (activeTab == "CONFIRMED") activeText else inactiveText)
        btnTabCompleted.setTextColor(if (activeTab == "COMPLETED") activeText else inactiveText)
    }

    private fun updateStatus(booking: Booking, status: String) {
        val token = prefsManager.getAuthToken() ?: return
        val request = UpdateSitterBookingStatusRequest(status = status)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateSitterBookingStatus("Bearer $token", booking.bookingId, request)
                }

                if (!isAdded) return@launch

                if (response.isSuccessful) {
                    loadRequests()
                } else {
                    showToast("Failed to update request")
                }
            } catch (e: Exception) {
                showToast("Error updating request")
            }
        }
    }

    private fun showToast(message: String) {
        context?.let { android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show() }
    }
}
