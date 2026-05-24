package com.example.mobile.features.bookings

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.R
import com.example.mobile.features.pets.Pet
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CreateBookingActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager

    private lateinit var tvSitterName: TextView
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etNotes: EditText
    private lateinit var spService: android.widget.Spinner
    private lateinit var petsContainer: LinearLayout
    private lateinit var btnSubmit: MaterialButton

    private var sitterId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_booking)

        prefsManager = PreferencesManager(this)
        sitterId = intent.getStringExtra(EXTRA_SITTER_ID)

        tvSitterName = findViewById(R.id.tvBookingSitterName)
        etDate = findViewById(R.id.etBookingDate)
        etStartTime = findViewById(R.id.etBookingStart)
        etEndTime = findViewById(R.id.etBookingEnd)
        etNotes = findViewById(R.id.etBookingNotes)
        spService = findViewById(R.id.spBookingService)
        petsContainer = findViewById(R.id.petCheckboxContainer)
        btnSubmit = findViewById(R.id.btnSubmitBooking)

        val sitterName = intent.getStringExtra(EXTRA_SITTER_NAME) ?: "Selected Sitter"
        tvSitterName.text = sitterName

        setupServiceSpinner()
        setupPickers()
        loadPets()

        btnSubmit.setOnClickListener { submitBooking() }
    }

    private fun setupServiceSpinner() {
        val serviceOptions = listOf("WALK", "FEEDING", "OVERNIGHT", "PLAYTIME")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, serviceOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spService.adapter = adapter
    }

    private fun setupPickers() {
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }
    }

    private fun showTimePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            target.setText(String.format("%02d:%02d:00", hour, minute))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun loadPets() {
        val token = prefsManager.getAuthToken() ?: return

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getMyPets("Bearer $token")
                }
                if (response.isSuccessful) {
                    val pets = response.body() ?: emptyList()
                    renderPetCheckboxes(pets)
                } else {
                    Toast.makeText(this@CreateBookingActivity, "Failed to load pets", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateBookingActivity, "Error loading pets", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderPetCheckboxes(pets: List<Pet>) {
        petsContainer.removeAllViews()
        if (pets.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "Add a pet before booking"
            petsContainer.addView(emptyText)
            return
        }

        pets.forEach { pet ->
            val checkbox = MaterialCheckBox(this)
            checkbox.text = pet.name
            checkbox.tag = pet.petId
            petsContainer.addView(checkbox)
        }
    }

    private fun submitBooking() {
        val token = prefsManager.getAuthToken() ?: return
        val sitter = sitterId ?: return

        val date = etDate.text.toString().trim()
        val startTime = etStartTime.text.toString().trim()
        val endTime = etEndTime.text.toString().trim()

        if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Date and time are required", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPetIds = (0 until petsContainer.childCount)
            .mapNotNull { index -> petsContainer.getChildAt(index) as? MaterialCheckBox }
            .filter { it.isChecked }
            .mapNotNull { it.tag as? String }

        if (selectedPetIds.isEmpty()) {
            Toast.makeText(this, "Select at least one pet", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateBookingRequest(
            sitterId = sitter,
            petIds = selectedPetIds,
            serviceType = spService.selectedItem.toString(),
            date = date,
            startTime = startTime,
            endTime = endTime,
            specialInstructions = etNotes.text.toString().trim().ifEmpty { null }
        )

        btnSubmit.isEnabled = false

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.createBooking("Bearer $token", request)
                }
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateBookingActivity, "Booking requested", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateBookingActivity, "Failed to create booking", Toast.LENGTH_SHORT).show()
                    btnSubmit.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateBookingActivity, "Error creating booking", Toast.LENGTH_SHORT).show()
                btnSubmit.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val EXTRA_SITTER_ID = "extra_sitter_id"
        private const val EXTRA_SITTER_NAME = "extra_sitter_name"

        fun newIntent(context: Context, sitterId: String, sitterName: String?): Intent {
            return Intent(context, CreateBookingActivity::class.java).apply {
                putExtra(EXTRA_SITTER_ID, sitterId)
                if (sitterName != null) {
                    putExtra(EXTRA_SITTER_NAME, sitterName)
                }
            }
        }
    }
}
