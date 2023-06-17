package com.example.apz_mobile

import androidx.appcompat.app.AppCompatActivity
import ApiService
import LoginRequest
import android.content.Intent

import kotlinx.coroutines.launch
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.apz_mobile.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val loginRequest = LoginRequest(login, password)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = apiService.login(loginRequest)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val accessToken = responseBody.access_token
                            saveAccessToken(accessToken)
                            getUserData()
                            val intent = Intent(this@LoginActivity, BuildingActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        // Handle error response
                        Log.e(TAG, "Error: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    // Handle connection or other issues
                    Log.e(TAG, "Request failed: ${e.message}")
                }
            }
        }
        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val languageCode = if (isChecked) {
                "uk"
            } else {
                "en"
            }
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            setUI()
        }

        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUI() {
        binding.textViewRegister.text = resources.getString(R.string.register_new_account)
        binding.loginButton.text = resources.getString(R.string.log_in)
        binding.loginEditText.hint = resources.getString(R.string.login)
        binding.passwordEditText.hint = resources.getString(R.string.password)
    }

    private fun getUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            // Get access_token from local storage
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val accessToken = sharedPreferences.getString("access_token", null)

            if (accessToken != null) {
                // Add "Bearer" prefix to access_token
                val authorization = "Bearer $accessToken"

                // Make API request
                val response = apiService.getUserData(authorization)

                if (response.isSuccessful) {
                    val userData = response.body()
                    if (userData != null) {
                        // Get user ID from response data
                        val userId = userData.id.oid

                        // Save user ID to local storage
                        with(sharedPreferences.edit()) {
                            putString("user_id", userId)
                            apply()
                        }

                        // Log user ID
                        Log.e(TAG, "User ID: $userId")
                    }
                } else {
                    // Handle error
                    Log.e(TAG, "Error: ${response.errorBody()?.string()}")
                }
            } else {
                // Handle missing access_token
                Log.e(TAG, "Error: access_token not found")
            }
        }
    }


    private fun saveAccessToken(accessToken: String) {
        // Save access_token to local storage, e.g., using SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("access_token", accessToken)
        editor.apply()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}