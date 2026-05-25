package com.example.mobile.features.sitters

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile.LoginActivity
import com.example.mobile.R
import com.example.mobile.network.RetrofitClient
import com.example.mobile.network.SitterProfileResponse
import com.example.mobile.network.SitterProfileUpdateRequest
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SitterProfileFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager

    private lateinit var tvHeader: TextView
    private lateinit var tvStatus: TextView

    private lateinit var etBio: EditText
    private lateinit var etExperience: EditText
    private lateinit var etLocation: EditText
    private lateinit var etRate: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etReferenceContact: EditText
    private lateinit var etVerificationUrl: EditText

    private lateinit var btnServiceWalk: MaterialButton
    private lateinit var btnServiceFeeding: MaterialButton
    private lateinit var btnServiceOvernight: MaterialButton
    private lateinit var btnServicePlaytime: MaterialButton

    private lateinit var etMonStart: EditText
    private lateinit var etMonEnd: EditText
    private lateinit var etTueStart: EditText
    private lateinit var etTueEnd: EditText
    private lateinit var etWedStart: EditText
    private lateinit var etWedEnd: EditText
    private lateinit var etThuStart: EditText
    private lateinit var etThuEnd: EditText
    private lateinit var etFriStart: EditText
    private lateinit var etFriEnd: EditText
    private lateinit var etSatStart: EditText
    private lateinit var etSatEnd: EditText
    private lateinit var etSunStart: EditText
    private lateinit var etSunEnd: EditText

    private lateinit var btnSave: MaterialButton
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnLogout: MaterialButton

    private val selectedServices = mutableSetOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sitter_profile, container, false)
        prefsManager = PreferencesManager(requireContext())

        tvHeader = view.findViewById(R.id.tvSitterProfileHeader)
        tvStatus = view.findViewById(R.id.tvSitterProfileStatus)

        etBio = view.findViewById(R.id.etSitterBio)
        etExperience = view.findViewById(R.id.etSitterExperience)
        etLocation = view.findViewById(R.id.etSitterLocation)
        etRate = view.findViewById(R.id.etSitterRate)
        etStudentId = view.findViewById(R.id.etSitterStudentId)
        etReferenceContact = view.findViewById(R.id.etSitterReference)
        etVerificationUrl = view.findViewById(R.id.etSitterVerification)

        btnServiceWalk = view.findViewById(R.id.btnServiceWalk)
        btnServiceFeeding = view.findViewById(R.id.btnServiceFeeding)
        btnServiceOvernight = view.findViewById(R.id.btnServiceOvernight)
        btnServicePlaytime = view.findViewById(R.id.btnServicePlaytime)

        etMonStart = view.findViewById(R.id.etMonStart)
        etMonEnd = view.findViewById(R.id.etMonEnd)
        etTueStart = view.findViewById(R.id.etTueStart)
        etTueEnd = view.findViewById(R.id.etTueEnd)
        etWedStart = view.findViewById(R.id.etWedStart)
        etWedEnd = view.findViewById(R.id.etWedEnd)
        etThuStart = view.findViewById(R.id.etThuStart)
        etThuEnd = view.findViewById(R.id.etThuEnd)
        etFriStart = view.findViewById(R.id.etFriStart)
        etFriEnd = view.findViewById(R.id.etFriEnd)
        etSatStart = view.findViewById(R.id.etSatStart)
        etSatEnd = view.findViewById(R.id.etSatEnd)
        etSunStart = view.findViewById(R.id.etSunStart)
        etSunEnd = view.findViewById(R.id.etSunEnd)

        btnSave = view.findViewById(R.id.btnSaveSitterProfile)
        btnSubmit = view.findViewById(R.id.btnSubmitVerification)
        btnLogout = view.findViewById(R.id.btnSitterLogout)

        btnServiceWalk.setOnClickListener { toggleService("WALK") }
        btnServiceFeeding.setOnClickListener { toggleService("FEEDING") }
        btnServiceOvernight.setOnClickListener { toggleService("OVERNIGHT") }
        btnServicePlaytime.setOnClickListener { toggleService("PLAYTIME") }

        btnSave.setOnClickListener { saveProfile() }
        btnSubmit.setOnClickListener { submitVerification() }
        btnLogout.setOnClickListener {
            prefsManager.clearAll()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        loadProfile()
        return view
    }

    private fun toggleService(service: String) {
        if (selectedServices.contains(service)) {
            selectedServices.remove(service)
        } else {
            selectedServices.add(service)
        }
        updateServiceButtons()
    }

    private fun updateServiceButtons() {
        val activeTint = requireContext().getColor(R.color.gray_dark)
        val inactiveTint = requireContext().getColor(R.color.soft_peach)
        val activeText = requireContext().getColor(R.color.web_surface)
        val inactiveText = requireContext().getColor(R.color.gray_dark)

        updateServiceButton(btnServiceWalk, "WALK", activeTint, inactiveTint, activeText, inactiveText)
        updateServiceButton(btnServiceFeeding, "FEEDING", activeTint, inactiveTint, activeText, inactiveText)
        updateServiceButton(btnServiceOvernight, "OVERNIGHT", activeTint, inactiveTint, activeText, inactiveText)
        updateServiceButton(btnServicePlaytime, "PLAYTIME", activeTint, inactiveTint, activeText, inactiveText)
    }

    private fun updateServiceButton(
        button: MaterialButton,
        service: String,
        activeTint: Int,
        inactiveTint: Int,
        activeText: Int,
        inactiveText: Int
    ) {
        val active = selectedServices.contains(service)
        button.backgroundTintList = ColorStateList.valueOf(if (active) activeTint else inactiveTint)
        button.setTextColor(if (active) activeText else inactiveText)
    }

    private fun loadProfile() {
        val token = prefsManager.getAuthToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val meResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getCurrentUser("Bearer $token")
                }
                if (!isAdded) return@launch

                if (meResponse.isSuccessful && meResponse.body() != null) {
                    val me = meResponse.body()!!
                    tvHeader.text = "${me.firstName} ${me.lastName}"
                    tvStatus.text = if (me.isVerified == true) "Verified" else "Pending Verification"

                    val profile = fetchProfile(token) ?: return@launch
                    bindProfile(profile, me.address)
                } else {
                    showToast("Failed to load profile")
                }
            } catch (e: Exception) {
                showToast("Failed to load profile")
            }
        }
    }

    private suspend fun fetchProfile(token: String): SitterProfileResponse? {
        val primary = withContext(Dispatchers.IO) {
            RetrofitClient.apiService.getSitterProfile("Bearer $token")
        }
        if (primary.isSuccessful && primary.body() != null) {
            return primary.body()
        }
        val secondary = withContext(Dispatchers.IO) {
            RetrofitClient.apiService.getSitterProfileAlt("Bearer $token")
        }
        return if (secondary.isSuccessful) secondary.body() else null
    }

    private fun bindProfile(profile: SitterProfileResponse, fallbackLocation: String?) {
        etBio.setText(profile.bio ?: "")
        etExperience.setText(profile.experience ?: "")
        etLocation.setText(profile.location ?: fallbackLocation ?: "")
        etRate.setText(profile.hourlyRate?.toString() ?: "")
        etStudentId.setText(profile.studentId ?: "")
        etReferenceContact.setText(profile.referenceContact ?: "")
        etVerificationUrl.setText(profile.verificationDocumentUrl ?: "")

        selectedServices.clear()
        profile.servicesOffered?.forEach { selectedServices.add(it.uppercase()) }
        updateServiceButtons()

        setDayTimes(profile.availabilitySchedule?.get("Monday"), etMonStart, etMonEnd)
        setDayTimes(profile.availabilitySchedule?.get("Tuesday"), etTueStart, etTueEnd)
        setDayTimes(profile.availabilitySchedule?.get("Wednesday"), etWedStart, etWedEnd)
        setDayTimes(profile.availabilitySchedule?.get("Thursday"), etThuStart, etThuEnd)
        setDayTimes(profile.availabilitySchedule?.get("Friday"), etFriStart, etFriEnd)
        setDayTimes(profile.availabilitySchedule?.get("Saturday"), etSatStart, etSatEnd)
        setDayTimes(profile.availabilitySchedule?.get("Sunday"), etSunStart, etSunEnd)
    }

    private fun setDayTimes(day: SitterProfileResponse.DayAvailability?, start: EditText, end: EditText) {
        start.setText(day?.startTime ?: "")
        end.setText(day?.endTime ?: "")
    }

    private fun saveProfile() {
        val token = prefsManager.getAuthToken() ?: return

        val request = SitterProfileUpdateRequest(
            profilePhotoUrl = null,
            bio = etBio.text.toString().trim().ifEmpty { null },
            experience = etExperience.text.toString().trim().ifEmpty { null },
            hourlyRate = etRate.text.toString().trim().toDoubleOrNull(),
            servicesOffered = selectedServices.toList(),
            availabilitySchedule = buildSchedule(),
            location = etLocation.text.toString().trim().ifEmpty { null },
            studentId = etStudentId.text.toString().trim().ifEmpty { null },
            referenceContact = etReferenceContact.text.toString().trim().ifEmpty { null },
            verificationDocumentUrl = etVerificationUrl.text.toString().trim().ifEmpty { null }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateSitterProfile("Bearer $token", request)
                }
                val updated = if (response.isSuccessful) response else withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.updateSitterProfileAlt("Bearer $token", request)
                }

                if (!isAdded) return@launch

                if (updated.isSuccessful) {
                    showToast("Profile saved")
                    loadProfile()
                } else {
                    showToast("Failed to save profile")
                }
            } catch (e: Exception) {
                showToast("Failed to save profile")
            }
        }
    }

    private fun submitVerification() {
        val token = prefsManager.getAuthToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.submitSitterVerification("Bearer $token")
                }
                val updated = if (response.isSuccessful) response else withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.submitSitterVerificationAlt("Bearer $token")
                }

                if (!isAdded) return@launch

                if (updated.isSuccessful) {
                    showToast("Verification submitted")
                    loadProfile()
                } else {
                    showToast("Failed to submit verification")
                }
            } catch (e: Exception) {
                showToast("Failed to submit verification")
            }
        }
    }

    private fun buildSchedule(): Map<String, SitterProfileResponse.DayAvailability> {
        return mapOf(
            "Monday" to SitterProfileResponse.DayAvailability(etMonStart.text.toString(), etMonEnd.text.toString()),
            "Tuesday" to SitterProfileResponse.DayAvailability(etTueStart.text.toString(), etTueEnd.text.toString()),
            "Wednesday" to SitterProfileResponse.DayAvailability(etWedStart.text.toString(), etWedEnd.text.toString()),
            "Thursday" to SitterProfileResponse.DayAvailability(etThuStart.text.toString(), etThuEnd.text.toString()),
            "Friday" to SitterProfileResponse.DayAvailability(etFriStart.text.toString(), etFriEnd.text.toString()),
            "Saturday" to SitterProfileResponse.DayAvailability(etSatStart.text.toString(), etSatEnd.text.toString()),
            "Sunday" to SitterProfileResponse.DayAvailability(etSunStart.text.toString(), etSunEnd.text.toString())
        )
    }

    private fun showToast(message: String) {
        context?.let { android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show() }
    }
}
