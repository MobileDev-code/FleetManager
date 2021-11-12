package com.example.fleetmanager.ui.home.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fleetmanager.R
import kotlinx.android.synthetic.main.driver_item.view.*

class DriversRecyclerAdapter(private val drivers: List<String>, val selectListener: (String) -> Unit) : RecyclerView.Adapter<DriversRecyclerAdapter.DriversViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriversViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.driver_item, parent, false)
        return DriversViewHolder(view)
    }

    override fun getItemCount(): Int = drivers.size

    override fun onBindViewHolder(holder: DriversViewHolder, position: Int) {
        val driver = drivers[position]
        holder.bind(driver)
    }

    inner class DriversViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                selectListener.invoke(drivers[adapterPosition])
            }
        }

        fun bind(driver: String) {
            view.driverTextView.text = driver
        }
    }
}