package com.c23ps021.capstoneprojects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ListProgressAdapter(val listProgress: ArrayList<List>): RecyclerView.Adapter<ListProgressAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_progress,parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (squad, bicep, deadlift) = listProgress[position]
        holder.tvSquad.text = squad
        holder.tvBicep.text = bicep.toString()
        holder.tvDeadlift.text = deadlift
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Kamu memilih " + listProgress[holder.adapterPosition].squad, Toast.LENGTH_SHORT).show()

        }

    }

    override fun getItemCount(): Int = listProgress.size
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSquad: TextView = itemView.findViewById(R.id.tv_reps_squad)
        var tvBicep: TextView = itemView.findViewById(R.id.tv_reps_bicep)
        var tvDeadlift: TextView = itemView.findViewById(R.id.tv_reps_deadlift)
    }
}