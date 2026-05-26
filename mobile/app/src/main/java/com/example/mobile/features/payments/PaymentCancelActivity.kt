package com.example.mobile.features.payments

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.R
import com.example.mobile.features.dashboard.OwnerMainActivity
import com.google.android.material.button.MaterialButton

class PaymentCancelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_cancel)

        val btnBack = findViewById<MaterialButton>(R.id.btnPaymentCancelBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, OwnerMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}