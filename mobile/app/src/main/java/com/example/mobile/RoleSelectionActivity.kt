package com.example.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class RoleSelectionActivity : Activity() {
    private lateinit var ownerCard: LinearLayout
    private lateinit var sitterCard: LinearLayout
    private lateinit var btnContinue: Button
    private lateinit var tvBack: TextView
    private var selectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        ownerCard = findViewById(R.id.cardOwner)
        sitterCard = findViewById(R.id.cardSitter)
        btnContinue = findViewById(R.id.btnContinue)
        tvBack = findViewById(R.id.tvBack)

        btnContinue.isEnabled = false

        tvBack.setOnClickListener {
            finish()
        }

        ownerCard.setOnClickListener {
            selectRole("PET_OWNER")
        }

        sitterCard.setOnClickListener {
            selectRole("PET_SITTER")
        }

        btnContinue.setOnClickListener {
            when(selectedRole) {
                "PET_OWNER" -> {
                    val intent = Intent(this, OwnerRegisterActivity::class.java)
                    startActivity(intent)
                }
                "PET_SITTER" -> {
                    val intent = Intent(this, SitterRegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role
        btnContinue.isEnabled = true

        if (role == "PET_OWNER") {
            ownerCard.setBackgroundResource(R.drawable.role_card_selected)
            sitterCard.setBackgroundResource(R.drawable.role_card)
        } else {
            sitterCard.setBackgroundResource(R.drawable.role_card_selected)
            ownerCard.setBackgroundResource(R.drawable.role_card)
        }
    }
}
