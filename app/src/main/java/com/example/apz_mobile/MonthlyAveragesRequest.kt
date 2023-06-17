package com.example.apz_mobile

data class MonthlyAveragesRequest(
    val _id: String,
    val avg_electricity_count: Double,
    val avg_gas_count: Double,
    val avg_temperature_count: Double,
    val avg_water_count: Double,
    val date: String,
    val month: String,
    val user_id: String
)
