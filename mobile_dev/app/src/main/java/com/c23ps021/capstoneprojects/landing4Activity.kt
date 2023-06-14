package com.c23ps021.capstoneprojects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c23ps021.capstoneprojects.databinding.ActivityLanding4Binding

class landing4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLanding4Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLanding4Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonBackLanding4.setOnClickListener {
            val intent = Intent(this, landing3Activity::class.java)
            startActivity(intent)
        }
        binding.buttonEdit.setOnClickListener {
            val intent = Intent(this, home_page::class.java)
            startActivity(intent)
        }
    }
}