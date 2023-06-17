package com.example.apz_mobile

import BuildingRequest
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BuildingAdapter(private val buildingList: List<BuildingRequest>) :
    RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder>() {

    class BuildingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val userIdTextView: TextView = itemView.findViewById(R.id.userIdTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return BuildingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int) {
        val currentBuilding = buildingList[position]
        holder.addressTextView.text = currentBuilding.address
        holder.userIdTextView.text = currentBuilding.user_id

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DailyActivity::class.java)
            intent.putExtra("user_id", currentBuilding.user_id)
            intent.putExtra("address", currentBuilding.address)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return buildingList.size
    }
}
