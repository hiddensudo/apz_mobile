package com.example.apz_mobile

import ApiService
import RegisterRequest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.apz_mobile.databinding.RegisterBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    private val binding: RegisterBinding by lazy {
        RegisterBinding.inflate(layoutInflater)
    }

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString()
            val lastName = binding.lastNameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val registerRequest = RegisterRequest(firstName, lastName, email, password)

            lifecycleScope.launch {
                try {
                    val response = apiService.register(registerRequest)
                    if (response.isSuccessful) {
                        navigateToLoginActivity()
                    } else {
                        // Handle registration failure
                    }
                } catch (e: Exception) {
                    // Handle registration request failure
                }
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
