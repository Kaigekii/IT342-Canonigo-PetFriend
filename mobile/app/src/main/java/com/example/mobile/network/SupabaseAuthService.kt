package com.example.mobile.network

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SupabaseAuthService(
    private val supabaseUrl: String,
    private val supabaseAnonKey: String
) {
    private val client = OkHttpClient()
    private val gson = Gson()

    @Throws(IOException::class)
    fun exchangeGoogleIdToken(idToken: String): SupabaseTokenResponse {
        val requestBody = gson.toJson(SupabaseTokenRequest(provider = "google", id_token = idToken))
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$supabaseUrl/auth/v1/token?grant_type=id_token")
            .addHeader("apikey", supabaseAnonKey)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                throw IOException("Supabase token exchange failed: ${response.code} ${errorBody}")
            }
            val body = response.body?.string().orEmpty()
            return gson.fromJson(body, SupabaseTokenResponse::class.java)
        }
    }
}
