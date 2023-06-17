package com.example.apz_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apz_mobile.databinding.ItemMonthlyAveragesBinding

class MonthlyAveragesAdapter(private val monthlyAveragesList: List<MonthlyAveragesRequest>) :
    RecyclerView.Adapter<MonthlyAveragesAdapter.MonthlyAveragesViewHolder>() {

    class MonthlyAveragesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val avgElectricityCountTextView: TextView = itemView.findViewById(R.id.avgElectricityCountTextView)
        val avgGasCountTextView: TextView = itemView.findViewById(R.id.avgGasCountTextView)
        val avgTemperatureCountTextView: TextView = itemView.findViewById(R.id.avgTemperatureCountTextView)
        val avgWaterCountTextView: TextView = itemView.findViewById(R.id.avgWaterCountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyAveragesViewHolder {
        val binding = ItemMonthlyAveragesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonthlyAveragesViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MonthlyAveragesViewHolder, position: Int) {
        val currentMonthlyAverage = monthlyAveragesList[position]
        holder.dateTextView.text = currentMonthlyAverage.date
        holder.avgElectricityCountTextView.text = currentMonthlyAverage.avg_electricity_count.toString()
        holder.avgGasCountTextView.text = currentMonthlyAverage.avg_gas_count.toString()
        holder.avgTemperatureCountTextView.text = currentMonthlyAverage.avg_temperature_count.toString()
        holder.avgWaterCountTextView.text = currentMonthlyAverage.avg_water_count.toString()
    }

    override fun getItemCount(): Int {
        return monthlyAveragesList.size
    }
}