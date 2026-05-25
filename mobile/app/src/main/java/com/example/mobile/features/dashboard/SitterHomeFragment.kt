package com.example.mobile.features.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.features.bookings.Booking
import com.example.mobile.features.bookings.SitterRequestAdapter
import com.example.mobile.features.bookings.SitterScheduleAdapter
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SitterHomeFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvToday: RecyclerView
    private lateinit var rvUpcoming: RecyclerView
    private lateinit var rvPending: RecyclerView

    private lateinit var tvWelcome: TextView
    private lateinit var tvVerified: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvStatPending: TextView
    private lateinit var tvStatUpcoming: TextView
    private lateinit var tvStatCompleted: TextView
    private lateinit var tvStatEarnings: TextView

    private lateinit var todayAdapter: SitterScheduleAdapter
    private lateinit var upcomingAdapter: SitterScheduleAdapter
    private lateinit var pendingAdapter: SitterRequestAdapter

    private var allBookings: List<Booking> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sitter_home, container, false)
        prefsManager = PreferencesManager(requireContext())

        tvWelcome = view.findViewById(R.id.tvSitterWelcome)
        tvVerified = view.findViewById(R.id.tvSitterVerified)
        tvRating = view.findViewById(R.id.tvSitterRating)
        tvStatPending = view.findViewById(R.id.tvStatPending)
        tvStatUpcoming = view.findViewById(R.id.tvStatUpcoming)
        tvStatCompleted = view.findViewById(R.id.tvStatCompleted)
        tvStatEarnings = view.findViewById(R.id.tvStatEarnings)

        rvPending = view.findViewById(R.id.rvSitterPending)
        rvToday = view.findViewById(R.id.rvSitterToday)
        rvUpcoming = view.findViewById(R.id.rvSitterUpcoming)

        pendingAdapter = SitterRequestAdapter(emptyList(),
            onAccept = { booking -> updateStatus(booking, "CONFIRMED") },
            onDecline = { booking -> updateStatus(booking, "CANCELLED") },
            onComplete = { booking -> updateStatus(booking, "COMPLETED") }
        )
        rvPending.layoutManager = LinearLayoutManager(requireContext())
        rvPending.adapter = pendingAdapter

        todayAdapter = SitterScheduleAdapter(emptyList())
        rvToday.layoutManager = LinearLayoutManager(requireContext())
        rvToday.adapter = todayAdapter

        upcomingAdapter = SitterScheduleAdapter(emptyList())
        rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        rvUpcoming.adapter = upcomingAdapter

        loadDashboard()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun loadDashboard() {
        val token = prefsManager.getAuthToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val meResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getCurrentUser("Bearer $token")
                }
                if (!isAdded) return@launch

                if (meResponse.isSuccessful && meResponse.body() != null) {
                    val me = meResponse.body()!!
                    tvWelcome.text = "Welcome, ${me.firstName}"
                    tvVerified.text = if (me.isVerified == true) "Verified" else "Pending Verification"

                    val pendingResponse = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.getSitterPendingBookings("Bearer $token")
                    }
                    val upcomingResponse = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.getSitterUpcomingBookings("Bearer $token")
                    }
                    val todayResponse = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.getSitterTodayBookings("Bearer $token")
                    }
                    val allResponse = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.getSitterBookings("Bearer $token")
                    }
                    val reviewSummaryResponse = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.getSitterReviewSummary("Bearer $token", me.userId)
                    }

                    if (!isAdded) return@launch

                    val pending = if (pendingResponse.isSuccessful) pendingResponse.body() ?: emptyList() else emptyList()
                    val upcoming = if (upcomingResponse.isSuccessful) upcomingResponse.body() ?: emptyList() else emptyList()
                    val today = if (todayResponse.isSuccessful) todayResponse.body() ?: emptyList() else emptyList()
                    allBookings = if (allResponse.isSuccessful) allResponse.body() ?: emptyList() else emptyList()

                    pendingAdapter.updateData(pending)
                    todayAdapter.updateData(today)
                    upcomingAdapter.updateData(upcoming)

                    val completedCount = allBookings.count { it.status == "COMPLETED" }
                    val totalEarned = allBookings.filter { it.status == "COMPLETED" }
                        .sumOf { it.totalAmount ?: 0.0 }

                    tvStatPending.text = pending.size.toString()
                    tvStatUpcoming.text = upcoming.size.toString()
                    tvStatCompleted.text = completedCount.toString()
                    tvStatEarnings.text = "PHP ${String.format("%.2f", totalEarned)}"

                    if (reviewSummaryResponse.isSuccessful && reviewSummaryResponse.body() != null) {
                        val summary = reviewSummaryResponse.body()!!
                        tvRating.text = "${String.format("%.1f", summary.averageRating)} (${summary.reviewCount} reviews)"
                    } else {
                        tvRating.text = "0.0 (0 reviews)"
                    }
                } else {
                    showToast("Failed to load sitter profile")
                }
            } catch (e: Exception) {
                showToast("Error loading sitter dashboard")
            }
        }
    }

    private fun updateStatus(booking: Booking, status: String) {
        val token = prefsManager.getAuthToken() ?: return
        val request = com.example.mobile.features.bookings.UpdateSitterBookingStatusRequest(status = status)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateSitterBookingStatus("Bearer $token", booking.bookingId, request)
                }
                if (!isAdded) return@launch

                if (response.isSuccessful) {
                    loadDashboard()
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
