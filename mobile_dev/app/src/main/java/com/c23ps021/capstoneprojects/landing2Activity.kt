package com.c23ps021.capstoneprojects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c23ps021.capstoneprojects.databinding.ActivityLanding2Binding
import com.c23ps021.capstoneprojects.databinding.ActivityLandingBinding

class landing2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLanding2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLanding2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonBackLanding2.setOnClickListener {
            val intent = Intent(this, landingActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLanding2.setOnClickListener {
            val intent = Intent(this, landing3Activity::class.java)
            startActivity(intent)
        }
    }
}