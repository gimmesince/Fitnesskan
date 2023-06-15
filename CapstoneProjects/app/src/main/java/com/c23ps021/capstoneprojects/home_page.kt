package com.c23ps021.capstoneprojects

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c23ps021.capstoneprojects.databinding.ActivityHomePageBinding
import com.c23ps021.capstoneprojects.databinding.ActivityLanding4Binding

class home_page : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var rvUser: RecyclerView
    private val list = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.imageView.setOnClickListener {
            val intent = Intent(this, profile_page::class.java)
            startActivity(intent)
        }
        binding.progressList.setOnClickListener {
            val intent = Intent(this, progress_list::class.java)
            startActivity(intent)
        }

        binding.idFABAdd.setOnClickListener {
            val intent = Intent(this, AddVideosActivity::class.java)
            startActivity(intent)
        }

//        binding.searchView.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                try {
//                    adapterBarang.filter.filter(s)
//                }
//                catch (e:Exception){
//
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//        })

        rvUser = findViewById(R.id.item_user_recycleview)
        rvUser.setHasFixedSize(true)

        list.addAll(listUser)
        showRecyclerList()
    }

    private val listUser: ArrayList<User>
        @SuppressLint("Recycle")
        get() {
            val dataName = resources.getStringArray(R.array.dataname)
            val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
            val listHero = ArrayList<User>()
            for (i in dataName.indices) {
                val hero = User(dataName[i],dataPhoto.getResourceId(i, -1))
                listHero.add(hero)
            }
            return listHero
        }
    private fun showRecyclerList() {
        rvUser.layoutManager = LinearLayoutManager(this)
        val listHeroAdapter = ListUserAdapter(list)
        rvUser.adapter = listHeroAdapter
    }
}