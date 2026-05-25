package com.example.mobile.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile.LoginActivity
import com.example.mobile.R
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvBio: TextView
    private lateinit var btnLogout: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        prefsManager = PreferencesManager(requireContext())

        tvName = view.findViewById(R.id.tvProfileName)
        tvEmail = view.findViewById(R.id.tvProfileEmail)
        tvRole = view.findViewById(R.id.tvProfileRole)
        tvPhone = view.findViewById(R.id.tvProfilePhone)
        tvAddress = view.findViewById(R.id.tvProfileAddress)
        tvBio = view.findViewById(R.id.tvProfileBio)
        btnLogout = view.findViewById(R.id.btnProfileLogout)

        btnLogout.setOnClickListener {
            prefsManager.clearAll()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        bindProfile()
        loadProfile()
        return view
    }

    private fun bindProfile() {
        val firstName = prefsManager.getFirstName() ?: ""
        val lastName = prefsManager.getLastName() ?: ""
        tvName.text = "$firstName $lastName"
        tvEmail.text = prefsManager.getEmail() ?: ""
        tvRole.text = prefsManager.getRole() ?: "Not provided"
        tvPhone.text = prefsManager.getPhoneNumber() ?: "Not provided"
        tvAddress.text = prefsManager.getAddress() ?: "Not provided"
        tvBio.text = "Profile details are shown from your account."
    }

    private fun loadProfile() {
        val token = prefsManager.getAuthToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getCurrentUser("Bearer $token")
                }

                if (!isAdded) return@launch

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    prefsManager.saveUserData(
                        userId = user.userId,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        address = user.address,
                        role = user.role,
                        isVerified = user.isVerified
                    )
                    bindProfile()
                } else if (response.code() == 401) {
                    prefsManager.clearAll()
                    showToast("Session expired. Please login again.")
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                } else {
                    showToast("Unable to refresh profile")
                }
            } catch (e: Exception) {
                showToast("Unable to refresh profile")
            }
        }
    }

    private fun showToast(message: String) {
        context?.let { android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show() }
    }
}
