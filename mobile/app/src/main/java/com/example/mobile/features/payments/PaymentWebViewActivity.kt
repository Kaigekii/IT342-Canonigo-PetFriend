package com.example.mobile.features.payments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.R

class PaymentWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_webview)

        val checkoutUrl = intent.getStringExtra(EXTRA_CHECKOUT_URL).orEmpty()
        val webView = findViewById<WebView>(R.id.webviewPayment)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (isSuccessUrl(url)) {
                    startActivity(Intent(this@PaymentWebViewActivity, PaymentSuccessActivity::class.java))
                    finish()
                    return true
                }
                if (isCancelUrl(url)) {
                    startActivity(Intent(this@PaymentWebViewActivity, PaymentCancelActivity::class.java))
                    finish()
                    return true
                }
                return false
            }
        }

        if (checkoutUrl.isNotBlank()) {
            webView.loadUrl(checkoutUrl)
        } else {
            startActivity(Intent(this, PaymentCancelActivity::class.java))
            finish()
        }
    }

    private fun isSuccessUrl(url: String): Boolean {
        return url.contains("/payment/success") || url.contains("payment/success")
    }

    private fun isCancelUrl(url: String): Boolean {
        return url.contains("/payment/cancel") || url.contains("payment/cancel")
    }

    companion object {
        private const val EXTRA_CHECKOUT_URL = "extra_checkout_url"

        fun newIntent(context: Context, checkoutUrl: String): Intent {
            return Intent(context, PaymentWebViewActivity::class.java).apply {
                putExtra(EXTRA_CHECKOUT_URL, checkoutUrl)
            }
        }
    }
}