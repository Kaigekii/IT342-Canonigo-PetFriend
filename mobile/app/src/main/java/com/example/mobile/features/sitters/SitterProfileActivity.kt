package com.example.mobile.features.sitters

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.features.bookings.CreateBookingActivity
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SitterProfileActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager

    private lateinit var tvName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvBio: TextView
    private lateinit var tvExperience: TextView
    private lateinit var tvServices: TextView
    private lateinit var tvRate: TextView
    private lateinit var tvRating: TextView
    private lateinit var rvReviews: RecyclerView
    private lateinit var btnBook: MaterialButton

    private lateinit var reviewAdapter: ReviewAdapter

    private var sitterId: String? = null
    private var sitterName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitter_profile)

        prefsManager = PreferencesManager(this)
        sitterId = intent.getStringExtra(EXTRA_SITTER_ID)

        tvName = findViewById(R.id.tvSitterProfileName)
        tvLocation = findViewById(R.id.tvSitterProfileLocation)
        tvBio = findViewById(R.id.tvSitterProfileBio)
        tvExperience = findViewById(R.id.tvSitterProfileExperience)
        tvServices = findViewById(R.id.tvSitterProfileServices)
        tvRate = findViewById(R.id.tvSitterProfileRate)
        tvRating = findViewById(R.id.tvSitterProfileRating)
        rvReviews = findViewById(R.id.rvSitterReviews)
        btnBook = findViewById(R.id.btnBookSitter)

        reviewAdapter = ReviewAdapter(emptyList())
        rvReviews.layoutManager = LinearLayoutManager(this)
        rvReviews.adapter = reviewAdapter

        btnBook.setOnClickListener {
            val id = sitterId ?: return@setOnClickListener
            val intent = CreateBookingActivity.newIntent(this, id, sitterName)
            startActivity(intent)
        }

        loadProfile()
    }

    private fun loadProfile() {
        val token = prefsManager.getAuthToken() ?: return
        val id = sitterId ?: return

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getSitterDetails("Bearer $token", id)
                }
                if (response.isSuccessful && response.body() != null) {
                    val details = response.body()!!
                    sitterName = details.fullName

                    tvName.text = details.fullName
                    tvLocation.text = details.location ?: ""
                    tvBio.text = details.bio ?: "No bio provided"
                    tvExperience.text = details.experience ?: "Experience not listed"
                    tvServices.text = if (details.servicesOffered.isEmpty()) {
                        "No services listed"
                    } else {
                        details.servicesOffered.joinToString(", ")
                    }
                    val rate = details.hourlyRate?.let { "PHP ${it}/hr" } ?: "Rate not set"
                    tvRate.text = rate
                    val rating = details.rating?.toString() ?: "-"
                    val reviews = details.reviewCount?.toString() ?: "0"
                    tvRating.text = "Rating: $rating ($reviews)"
                    reviewAdapter.updateData(details.reviews)
                } else {
                    Toast.makeText(this@SitterProfileActivity, "Failed to load sitter", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SitterProfileActivity, "Error loading sitter", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        const val EXTRA_SITTER_ID = "extra_sitter_id"
    }
}
