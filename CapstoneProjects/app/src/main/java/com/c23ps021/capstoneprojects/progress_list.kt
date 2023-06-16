package com.c23ps021.capstoneprojects

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c23ps021.capstoneprojects.databinding.ActivityProgressListBinding

class progress_list : AppCompatActivity() {
    private lateinit var binding: ActivityProgressListBinding
    private lateinit var rvUser: RecyclerView
    private val list = ArrayList<List>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvUser = findViewById(R.id.item_progress_list_recycleview)
        rvUser.setHasFixedSize(true)

        list.addAll(list_progress)
        showRecyclerList()

        binding.btnBackProfile.setOnClickListener {
            val intent = Intent(this, home_page::class.java)
            startActivity(intent)
        }
    }

    private val list_progress: ArrayList<List>
        @SuppressLint("Recycle")
        get() {
            val dataSquad = resources.getStringArray(R.array.squad)
            val dataBicep = resources.getStringArray(R.array.bicep)
            val dataDead = resources.getStringArray(R.array.deadlift)
            val listHero = ArrayList<List>()
            for (i in dataSquad.indices) {
                val hero = List(dataSquad[i],dataBicep[i],dataDead[i])
                listHero.add(hero)
            }
            return listHero
        }
    private fun showRecyclerList() {
        rvUser.layoutManager = LinearLayoutManager(this)
        val listHeroAdapter = ListProgressAdapter(list)
        rvUser.adapter = listHeroAdapter
    }
}

