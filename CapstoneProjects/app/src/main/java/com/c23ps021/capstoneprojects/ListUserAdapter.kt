package com.c23ps021.capstoneprojects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ListUserAdapter(val listUser: ArrayList<User>): RecyclerView.Adapter<ListUserAdapter.ListViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item,parent, false)
            return ListViewHolder(view)
        }
        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            val (name,photo) = listUser[position]
            if (photo != null) {
                holder.imgPhoto.setImageResource(photo)
            }
            holder.tvName.text = name
            holder.itemView.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Kamu memilih " + listUser[holder.adapterPosition].name, Toast.LENGTH_SHORT).show()
            }
        }
        override fun getItemCount(): Int = listUser.size
        class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imgPhoto: ImageView = itemView.findViewById(R.id.iv_barang)
            var tvName: TextView = itemView.findViewById(R.id.explore_text)
        }
}