package com.c23ps021.capstoneprojects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c23ps021.capstoneprojects.databinding.ActivityLanding3Binding

class landing3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLanding3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLanding3Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonBackLanding3.setOnClickListener {
            val intent = Intent(this, landing2Activity::class.java)
            startActivity(intent)
        }
        binding.buttonLanding3.setOnClickListener {
            val intent = Intent(this, landing4Activity::class.java)
            startActivity(intent)
        }
    }
}