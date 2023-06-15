package com.c23ps021.capstoneprojects

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.c23ps021.capstoneprojects.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class splashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent= Intent(this,landingActivity::class.java)
            startActivity(intent)
        },3000)
    }

}