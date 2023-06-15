package com.c23ps021.capstoneprojects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c23ps021.capstoneprojects.databinding.ActivityProgressListBinding

class progress_list : AppCompatActivity() {
    private lateinit var binding: ActivityProgressListBinding
    private lateinit var rvUser: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvUser = findViewById(R.id.item_user_recycleview)
        rvUser.setHasFixedSize(true)
    }
}