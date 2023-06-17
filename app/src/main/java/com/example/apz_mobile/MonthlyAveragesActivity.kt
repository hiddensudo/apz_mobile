package com.example.apz_mobile

import ApiService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apz_mobile.databinding.MonthlyAveragesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MonthlyAveragesActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var monthlyAveragesAdapter: MonthlyAveragesAdapter // Custom adapter for monthly averages data
    private val monthlyAveragesList: MutableList<MonthlyAveragesRequest> =
        mutableListOf() // List to hold monthly averages data

    private val binding: MonthlyAveragesBinding by lazy {
        MonthlyAveragesBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/") // Замініть це на свій URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        binding.backButton.setOnClickListener {
            val intent = Intent(this, DailyActivity::class.java)
            startActivity(intent)
        }

        binding.analyticsButton.setOnClickListener {
            // Get user ID from local storage
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val userId = sharedPreferences.getString("user_id", null)

            if (userId != null) {
                // Create request object with user ID
                val request = ApiService.MonthlyAnalyticsRequest(user_id = userId)

                // Make API request
                lifecycleScope.launch(Dispatchers.IO) {
                    val response = apiService.makeMonthlyAnalytics(request)

                    if (response.isSuccessful) {
                        getMonthlyAveragesData(userId)
                    } else {
                        // Handle error
                    }
                }
            } else {
                // Handle missing user ID
            }
        }


        apiService = retrofit.create(ApiService::class.java)

        // Set up RecyclerView and its adapter
        monthlyAveragesAdapter =
            MonthlyAveragesAdapter(monthlyAveragesList) // Initialize adapter with empty list
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MonthlyAveragesActivity)
            adapter = monthlyAveragesAdapter
        }
        getUserData()
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

                        // Log user ID
                        Log.e("MonthlyAveragesActivity", "User ID: $userId")

                        // Get monthly averages data
                        getMonthlyAveragesData(userId)
                    }
                } else {
                    // Handle error
                    Log.e("MonthlyAveragesActivity", "Error: ${response.errorBody()?.string()}")
                }
            } else {
                // Handle missing access_token
                Log.e("MonthlyAveragesActivity", "Error: access_token not found")
            }
        }
    }

    private fun getMonthlyAveragesData(userId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Make API request
            val response = apiService.getMonthlyAveragesData(userId)

            if (response.isSuccessful) {
                val monthlyAveragesData = response.body()
                if (monthlyAveragesData != null) {
                    // Update RecyclerView data on the main thread
                    withContext(Dispatchers.Main) {
                        monthlyAveragesList.clear()
                        monthlyAveragesList.addAll(monthlyAveragesData)
                        monthlyAveragesAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                // Handle error
                Log.e("MonthlyAveragesActivity", "Error: ${response.errorBody()?.string()}")
            }
        }
    }
}
