package com.example.apz_mobile

import ApiService
import DailyRequest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apz_mobile.databinding.ActivityDailyBinding
import com.example.apz_mobile.databinding.ItemDailyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var dailyAdapter: DailyAdapter // Custom adapter for daily data
    private val dailyList: MutableList<DailyRequest> = mutableListOf() // List to hold daily data

    private val binding: ActivityDailyBinding by lazy {
        ActivityDailyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Set up logging
        val electricityTextView: TextView = findViewById(R.id.electricityTextView)
        val gasTextView: TextView = findViewById(R.id.gasTextView)
        val waterTextView: TextView = findViewById(R.id.waterTextView)
        Log.d("DailyActivity", "Electricity text view: $electricityTextView")
        Log.d("DailyActivity", "Gas text view: $gasTextView")
        Log.d("DailyActivity", "Water text view: $waterTextView")

        // Set up Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Set up button listeners
        val getAveragesButton: Button = findViewById(R.id.getAveragesButton)
        getAveragesButton.setOnClickListener {
            val intent = Intent(this, MonthlyAveragesActivity::class.java)
            startActivity(intent)
        }
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, BuildingActivity::class.java)
            startActivity(intent)
        }

        // Get user_id and building address from local storage
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        val buildingAddress = sharedPreferences.getString("building_address", null)

        // Update UI with building address
        if (buildingAddress != null) {
            binding.addressTextView.text = buildingAddress
        }

        // Load limit from local storage
        val limit = sharedPreferences.getString("limit", null)
        if (limit != null) {
            binding.limitEditText.setText(limit)
        }

        // Save limit to local storage when user changes it
        binding.limitEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val editor = sharedPreferences.edit()
                editor.putString("limit", s.toString())
                editor.apply()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Set up RecyclerView and its adapter
        dailyAdapter = DailyAdapter(dailyList) // Initialize adapter with empty list
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DailyActivity)
            adapter = dailyAdapter
        }

        // Fetch daily data from API
        if (userId != null) {
            fetchDailyData(userId)
        }
    }



    private fun fetchDailyData(userId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getDailyData(userId)
                if (response.isSuccessful) {
                    val dailyData = response.body()
                    if (dailyData != null) {


                        dailyList.addAll(dailyData)
                        dailyData.forEach { Log.d("DailyActivity", "Date: ${it.date}") }
                        runOnUiThread {
                            dailyAdapter.notifyDataSetChanged() // Notify adapter about data change
                        }

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DATE, -30)
                        val thirtyDaysAgo = calendar.time

                        val electricitySum =
                            dailyData.filter { dateFormat.parse(it.date).after(thirtyDaysAgo) }
                                .sumOf { it.electricity_count }
                        val gasSum =
                            dailyData.filter { dateFormat.parse(it.date).after(thirtyDaysAgo) }
                                .sumOf { it.gas_count }
                        val waterSum =
                            dailyData.filter { dateFormat.parse(it.date).after(thirtyDaysAgo) }
                                .sumOf { it.water_count }

                        runOnUiThread {
                            binding.electricityTextView.text = resources.getString(R.string.last_month_electricity_count) + " " +  electricitySum
                            binding.gasTextView.text = resources.getString(R.string.last_month_gas_count) + gasSum
                            binding.waterTextView.text = resources.getString(R.string.last_month_water_count) + waterSum
                        }

                        val electricityPrice = if (electricitySum <= 100) electricitySum * 0.9 else 100 * 0.9 + (electricitySum - 100) * 1.68
                        val gasPrice = gasSum * 23.40
                        val waterPrice = waterSum * 68.97
                        val totalPrice = electricityPrice + gasPrice + waterPrice

                        runOnUiThread {
                            binding.toBePaid.text = resources.getString(R.string.last_month_pay_count) + totalPrice
                        }

                        // Change text color based on limit
                        val limitString = binding.limitEditText.text.toString()
                        if (limitString.isNotEmpty()) {
                            val limit = limitString.toDoubleOrNull()
                            if (limit != null) {
                                if (limit < totalPrice) {
                                    binding.toBePaid.setTextColor(Color.RED)
                                } else {
                                    binding.toBePaid.setTextColor(Color.GREEN)
                                }
                            }
                        }

                    }
                } else {
                    // Handle unsuccessful response
                }

            } catch (e: Exception) {
                // Handle network or API call failure
            }
        }
    }
}
