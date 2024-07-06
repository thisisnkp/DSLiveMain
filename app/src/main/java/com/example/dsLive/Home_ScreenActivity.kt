package com.example.dsLive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dsLive.databinding.ActivityHomeScreenBinding

class Home_ScreenActivity : AppCompatActivity() {

private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityHomeScreenBinding.inflate(layoutInflater)
     setContentView(binding.root)

    }

}