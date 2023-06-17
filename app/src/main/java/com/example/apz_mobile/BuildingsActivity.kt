package com.example.apz_mobile

import ApiService
import BuildingRequest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apz_mobile.databinding.BuildingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BuildingActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var buildingAdapter: BuildingAdapter
    private val buildingList: MutableList<BuildingRequest> = mutableListOf()

    private lateinit var binding: BuildingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BuildingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        setupRecyclerView()

        fetchBuildingsData()
    }

    private fun setupRecyclerView() {
        buildingAdapter = BuildingAdapter(buildingList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@BuildingActivity)
            adapter = buildingAdapter
        }
    }

    private fun fetchBuildingsData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getAllBuildings()
                if (response.isSuccessful) {
                    val buildings = response.body()
                    if (buildings != null) {
                        buildingList.addAll(buildings)
                        runOnUiThread {
                            buildingAdapter.notifyDataSetChanged()
                        }

                        // Save first building address to local storage
                        val firstBuilding = buildings.firstOrNull()
                        if (firstBuilding != null) {
                            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("building_address", firstBuilding.address)
                                apply()
                            }
                        }
                    }
                } else {
                    Log.e("BuildingActivity", "Failed to fetch buildings data: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BuildingActivity", "Error fetching buildings data: ${e.message}")
            }
        }
    }

}