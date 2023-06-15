package com.c23ps021.capstoneprojects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c23ps021.capstoneprojects.databinding.ActivityLandingBinding

class landingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLanding1.setOnClickListener {
            val intent = Intent(this, landing2Activity::class.java)
            startActivity(intent)
        }
    }
}