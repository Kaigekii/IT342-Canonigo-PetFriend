package com.example.mobile.features.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile.LoginActivity
import com.example.mobile.R
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView
    private lateinit var btnLogout: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        prefsManager = PreferencesManager(requireContext())

        tvName = view.findViewById(R.id.tvProfileName)
        tvEmail = view.findViewById(R.id.tvProfileEmail)
        tvRole = view.findViewById(R.id.tvProfileRole)
        tvPhone = view.findViewById(R.id.tvProfilePhone)
        tvAddress = view.findViewById(R.id.tvProfileAddress)
        btnLogout = view.findViewById(R.id.btnProfileLogout)

        btnLogout.setOnClickListener {
            prefsManager.clearAll()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        bindProfile()
        return view
    }

    private fun bindProfile() {
        val firstName = prefsManager.getFirstName() ?: ""
        val lastName = prefsManager.getLastName() ?: ""
        tvName.text = "$firstName $lastName"
        tvEmail.text = prefsManager.getEmail() ?: ""
        tvRole.text = prefsManager.getRole() ?: ""
        tvPhone.text = prefsManager.getPhoneNumber() ?: "Not provided"
        tvAddress.text = prefsManager.getAddress() ?: "Not provided"
    }
}
