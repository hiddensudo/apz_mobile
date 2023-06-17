package com.example.apz_mobile

import DailyRequest
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DailyAdapter(private val dailyList: List<DailyRequest>) :
    RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

    class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val dayTemperatureTextView: TextView = itemView.findViewById(R.id.dayTemperatureTextView)
        val electricityCountTextView: TextView = itemView.findViewById(R.id.electricityCountTextView)
        val gasCountTextView: TextView = itemView.findViewById(R.id.gasCountTextView)
        val waterCountTextView: TextView = itemView.findViewById(R.id.waterCountTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily, parent, false)
        return DailyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentDaily = dailyList[position]
        holder.dateTextView.text = currentDaily.date
        holder.dayTemperatureTextView.text = "Day temperature: ${currentDaily.day_temperature}"
        holder.electricityCountTextView.text = "Electricity count:${currentDaily.electricity_count}"
        holder.gasCountTextView.text = "Gas count:${currentDaily.gas_count}"
        holder.waterCountTextView.text = "Water count:${currentDaily.water_count}"
    }

    override fun getItemCount(): Int {
        return dailyList.size
    }
}
