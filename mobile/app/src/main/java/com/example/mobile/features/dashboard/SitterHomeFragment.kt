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
import com.example.mobile.features.bookings.SitterScheduleAdapter
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SitterHomeFragment : Fragment() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvToday: RecyclerView
    private lateinit var rvUpcoming: RecyclerView

    private lateinit var todayAdapter: SitterScheduleAdapter
    private lateinit var upcomingAdapter: SitterScheduleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sitter_home, container, false)
        prefsManager = PreferencesManager(requireContext())

        rvToday = view.findViewById(R.id.rvSitterToday)
        rvUpcoming = view.findViewById(R.id.rvSitterUpcoming)

        todayAdapter = SitterScheduleAdapter(emptyList())
        rvToday.layoutManager = LinearLayoutManager(requireContext())
        rvToday.adapter = todayAdapter

        upcomingAdapter = SitterScheduleAdapter(emptyList())
        rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        rvUpcoming.adapter = upcomingAdapter

        loadSchedules()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadSchedules()
    }

    private fun loadSchedules() {
        val token = prefsManager.getAuthToken() ?: return

        scope.launch {
            try {
                val todayResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getSitterTodayBookings("Bearer $token")
                }
                val upcomingResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getSitterUpcomingBookings("Bearer $token")
                }

                if (todayResponse.isSuccessful) {
                    todayAdapter.updateData(todayResponse.body() ?: emptyList())
                }
                if (upcomingResponse.isSuccessful) {
                    upcomingAdapter.updateData(upcomingResponse.body() ?: emptyList())
                }

                if (!todayResponse.isSuccessful || !upcomingResponse.isSuccessful) {
                    Toast.makeText(requireContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading schedule", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}
